data "aws_ecr_repository" "index-svc-ecr" {
  name = "${var.repository_name}"
}

resource "aws_ecr_lifecycle_policy" "index-svc-ecr-lifecycle-policy" {
  repository = "${data.aws_ecr_repository.index-svc-ecr.name}"
  policy     = "${file("${path.module}/ecr-lifecycle-policy.json")}"
  depends_on = ["data.aws_ecr_repository.index-svc-ecr"]
}

resource "aws_cloudwatch_log_group" "index_svc" {
  name = "${var.environment}-index_svc"

  tags {
    Environment = "${var.environment}"
    Application = "SURS Indexing Module"
  }
}

data "aws_elb" "elasticsearch_lb" {
  name = "${var.environment}-elastic-search-elb"
}

data "template_file" "index_svc_task" {
  template = "${file("${path.module}/index_svc_task.json")}"

  vars {
    image             = "${data.aws_ecr_repository.index-svc-ecr.repository_url}"
    tag               = "${var.tag}"
    env               = "${var.environment}"
    db_pass           = "${var.db_pass}"
    mongo_pass        = "${var.mongo_pass}"
    admin_pass        = "${var.admin_pass}"
    log_group         = "${aws_cloudwatch_log_group.index_svc.name}"
    region            = "${var.region}"
    kafka_internal_ip = "${var.kafka_internal_ip}"
    elasticsearch     = "${data.aws_elb.elasticsearch_lb.dns_name}"
  }

  depends_on = [
    "data.aws_ecr_repository.index-svc-ecr",
  ]
}

resource "aws_ecs_task_definition" "index_svc" {
  family                = "${var.environment}_index"
  container_definitions = "${data.template_file.index_svc_task.rendered}"
  execution_role_arn    = "${var.ecs_task_execution_role_arn}"
  task_role_arn         = "${var.ecs_task_execution_role_arn}"

  depends_on = [
    "data.template_file.index_svc_task",
  ]
}

resource "aws_ecs_service" "index-svc-service" {
  name            = "${var.environment}-index"
  cluster         = "${var.ecs_cluster_id}"
  task_definition = "${aws_ecs_task_definition.index_svc.family}:${max("${aws_ecs_task_definition.index_svc.revision}", "${data.aws_ecs_task_definition.index_svc.revision}")}"
  desired_count   = "${var.desired_count}"
  iam_role        = "${var.ecs_service_role_arn}"

  load_balancer {
    target_group_arn = "${aws_alb_target_group.index-alb-target.arn}"
    container_name   = "${var.environment}-index-svc"
    container_port   = 9101
  }

  depends_on = [
    "aws_alb_target_group.index-alb-target",
    "aws_ecs_task_definition.index_svc",
    "data.aws_ecs_task_definition.index_svc",
  ]
}

resource "aws_route53_record" "index_svc_route53" {
  zone_id = "${var.route53_zone_id}"
  name    = "index-${var.environment}"
  type    = "A"

  alias {
    name                   = "${data.aws_alb.sbirone-alb.dns_name}"
    zone_id                = "${data.aws_alb.sbirone-alb.zone_id}"
    evaluate_target_health = true
  }

  depends_on = [
    "data.aws_alb.sbirone-alb",
  ]
}

/*====
Application Load Balancer
======*/

data aws_subnet_ids public_subnet_ids {
  vpc_id = "${var.vpc-id}"

  tags {
    Name = "${var.environment} VPC-public-us-east-1*"
  }
}

data "aws_alb" sbirone-alb {
  name = "${var.environment}-alb"
}

data aws_alb_listener base-listener {
  load_balancer_arn = "${data.aws_alb.sbirone-alb.arn}"
  port              = 443

  depends_on = [
    "data.aws_alb.sbirone-alb",
  ]
}

resource "aws_lb_listener_rule" "https_listener_rule" {
  listener_arn = "${data.aws_alb_listener.base-listener.arn}"
  priority     = 104

  action {
    target_group_arn = "${aws_alb_target_group.index-alb-target.arn}"
    type             = "forward"
  }

  condition {
    field = "host-header"

    values = [
      "index-${var.environment}.*.bytecubed.io",
    ]
  }

  depends_on = [
    "data.aws_alb_listener.base-listener",
    "aws_alb_target_group.index-alb-target",
  ]
}

resource "aws_alb_target_group" "index-alb-target" {
  name     = "index-${var.environment}-alb-target"
  port     = 9101
  protocol = "HTTP"
  vpc_id   = "${var.vpc-id}"

  health_check {
    healthy_threshold   = 2
    unhealthy_threshold = 10
    protocol            = "HTTP"
    path                = "/ping"
    interval            = 5
    timeout             = 4
  }

  tags {
    Environment = "${var.environment}"
  }
}

/*====
AWS ECS Service
======*/

data aws_ecs_task_definition index_svc {
  task_definition = "${aws_ecs_task_definition.index_svc.family}"
}

/*====
ECS Service AutoScaling
======*/

resource "aws_appautoscaling_target" "index_svc_target" {
  service_namespace  = "ecs"
  resource_id        = "service/${var.ecs_cluster_name}/${aws_ecs_service.index-svc-service.name}"
  scalable_dimension = "ecs:service:DesiredCount"
  role_arn           = "${var.ecs_scaling_role_arn}"
  min_capacity       = "${(var.desired_count - 1) < 1 ? 1 : (var.desired_count - 1)}"
  max_capacity       = "${var.desired_count * 2}"
}

resource "aws_appautoscaling_policy" "index_svc_up" {
  name               = "${var.environment}_scale_up"
  service_namespace  = "ecs"
  resource_id        = "service/${var.ecs_cluster_name}/${aws_ecs_service.index-svc-service.name}"
  scalable_dimension = "ecs:service:DesiredCount"

  step_scaling_policy_configuration {
    adjustment_type         = "ChangeInCapacity"
    cooldown                = 60
    metric_aggregation_type = "Maximum"

    step_adjustment {
      metric_interval_lower_bound = 0
      scaling_adjustment          = 1
    }
  }

  depends_on = [
    "aws_appautoscaling_target.index_svc_target",
    "aws_ecs_service.index-svc-service",
  ]
}

resource "aws_appautoscaling_policy" "index_svc_down" {
  name               = "${var.environment}_scale_down"
  service_namespace  = "ecs"
  resource_id        = "service/${var.ecs_cluster_name}/${aws_ecs_service.index-svc-service.name}"
  scalable_dimension = "ecs:service:DesiredCount"

  step_scaling_policy_configuration {
    adjustment_type         = "ChangeInCapacity"
    cooldown                = 60
    metric_aggregation_type = "Maximum"

    step_adjustment {
      metric_interval_lower_bound = 0
      scaling_adjustment          = -1
    }
  }

  depends_on = [
    "aws_appautoscaling_target.index_svc_target",
    "aws_ecs_service.index-svc-service",
  ]
}

/* metric used for auto scale */
resource "aws_cloudwatch_metric_alarm" "index_svc_service_cpu_high" {
  alarm_name          = "SURS ${var.environment}_index_cpu_utilization_high"
  comparison_operator = "GreaterThanOrEqualToThreshold"
  evaluation_periods  = "2"
  metric_name         = "MemoryUtilization"
  namespace           = "AWS/ECS"
  period              = "60"
  statistic           = "Maximum"
  threshold           = "85"
  unit                = "Percent"

  dimensions {
    ClusterName = "${var.ecs_cluster_name}"
    ServiceName = "${aws_ecs_service.index-svc-service.name}"
  }

  alarm_actions = [
    "${aws_appautoscaling_policy.index_svc_up.arn}",
  ]

  ok_actions = [
    "${aws_appautoscaling_policy.index_svc_down.arn}",
  ]

  depends_on = [
    "aws_appautoscaling_policy.index_svc_up",
    "aws_appautoscaling_policy.index_svc_down",
    "aws_ecs_service.index-svc-service",
  ]
}
