data "aws_ecr_repository" "reporting-scala-ecr" {
  name = "${var.scala_repository_name}"
}

resource "aws_ecr_lifecycle_policy" "reporting-scala-ecr-lifecycle-policy" {
  repository = "${data.aws_ecr_repository.reporting-scala-ecr.name}"
  policy     = "${file("${path.module}/ecr-lifecycle-policy.json")}"
  depends_on = ["data.aws_ecr_repository.reporting-scala-ecr"]
}

data aws_ecr_repository reporting-java-ecr {
  name = "${var.java_repository_name}"
}

resource "aws_ecr_lifecycle_policy" "reporting-java-ecr-lifecycle-policy" {
  repository = "${data.aws_ecr_repository.reporting-java-ecr.name}"
  policy     = "${file("${path.module}/ecr-lifecycle-policy.json")}"
  depends_on = ["data.aws_ecr_repository.reporting-java-ecr"]
}

data aws_ecr_repository reporting-haproxy-ecr {
  name = "${var.haproxy_repository_name}"
}

resource "aws_ecr_lifecycle_policy" "reporting-haproxy-ecr-lifecycle-policy" {
  repository = "${data.aws_ecr_repository.reporting-haproxy-ecr.name}"
  policy     = "${file("${path.module}/ecr-lifecycle-policy.json")}"
  depends_on = ["data.aws_ecr_repository.reporting-haproxy-ecr"]
}

resource "aws_cloudwatch_log_group" "reporting" {
  name = "${var.environment}-reporting"

  tags {
    Environment = "${var.environment}"
    Application = "SURS Reporting Module"
  }
}

/*====
AWS ECS Service
======*/

data aws_ecs_task_definition reporting {
  task_definition = "${aws_ecs_task_definition.reporting.family}"
}

data "aws_elb" "elasticsearch_lb" {
  name = "${var.environment}-elastic-search-elb"
}

data "template_file" "reporting_task" {
  template = "${file("${path.module}/reporting_task.json")}"

  vars {
    scala_image           = "${data.aws_ecr_repository.reporting-scala-ecr.repository_url}"
    java_image            = "${data.aws_ecr_repository.reporting-java-ecr.repository_url}"
    haproxy_image         = "${data.aws_ecr_repository.reporting-haproxy-ecr.repository_url}"
    tag                   = "${var.tag}"
    env                   = "${var.environment}"
    log_group             = "${aws_cloudwatch_log_group.reporting.name}"
    region                = "${var.region}"
    access_key            = "${var.access_key}"
    secret_key            = "${var.secret_key}"
    mongo_pass            = "${var.mongo_pass}"
    mongo_user            = "${var.mongo_user}"
    auth_host_internal_ip = "${var.auth_host_internal_ip}"
    kafka_internal_ip     = "${var.kafka_internal_ip}"
    elasticsearch         = "${data.aws_elb.elasticsearch_lb.dns_name}"
  }

  depends_on = ["data.aws_ecr_repository.reporting-scala-ecr", "data.aws_ecr_repository.reporting-haproxy-ecr", "data.aws_ecr_repository.reporting-java-ecr"]
}

resource "aws_ecs_task_definition" "reporting" {
  family                = "${var.environment}_reporting"
  container_definitions = "${data.template_file.reporting_task.rendered}"
  execution_role_arn    = "${var.ecs_task_execution_role_arn}"
  task_role_arn         = "${var.ecs_task_execution_role_arn}"

  depends_on = [
    "data.template_file.reporting_task",
  ]
}

resource "aws_ecs_service" "reporting-service" {
  name            = "${var.environment}-reporting"
  cluster         = "${var.ecs_cluster_id}"
  task_definition = "${aws_ecs_task_definition.reporting.family}:${max("${aws_ecs_task_definition.reporting.revision}", "${data.aws_ecs_task_definition.reporting.revision}")}"
  desired_count   = "${var.desired_count}"
  iam_role        = "${var.ecs_service_role_arn}"

  load_balancer {
    target_group_arn = "${aws_alb_target_group.reporting-alb-target.arn}"
    container_name   = "${var.environment}-reporting-proxy"
    container_port   = 80
  }

  depends_on = [
    "aws_alb_target_group.reporting-alb-target",
    "aws_ecs_task_definition.reporting",
    "data.aws_ecs_task_definition.reporting",
  ]
}

resource "aws_route53_record" "reporting_route53" {
  zone_id = "${var.route53_zone_id}"
  name    = "surs-${var.environment}"
  type    = "A"

  alias {
    name                   = "${data.aws_alb.sbirone-alb.dns_name}"
    zone_id                = "${data.aws_alb.sbirone-alb.zone_id}"
    evaluate_target_health = true
  }

  depends_on = ["data.aws_alb.sbirone-alb"]
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

  depends_on = ["data.aws_alb.sbirone-alb"]
}

resource "aws_lb_listener_rule" "https_listener_rule" {
  listener_arn = "${data.aws_alb_listener.base-listener.arn}"
  priority     = 103

  action {
    target_group_arn = "${aws_alb_target_group.reporting-alb-target.arn}"
    type             = "forward"
  }

  condition {
    field  = "host-header"
    values = ["surs-${var.environment}.*.bytecubed.io"]
  }

  depends_on = ["data.aws_alb_listener.base-listener", "aws_alb_target_group.reporting-alb-target"]
}

resource "aws_alb_target_group" "reporting-alb-target" {
  name       = "reporting-${var.environment}-alb-target"
  port       = 80
  protocol   = "HTTP"
  vpc_id     = "${var.vpc-id}"
  slow_start = 30

  health_check {
    healthy_threshold   = 2
    unhealthy_threshold = 10
    protocol            = "HTTP"
    path                = "/health"
    interval            = 60
    timeout             = 15
  }

  tags {
    Environment = "${var.environment}"
  }
}

/*====
ECS Service AutoScaling
======*/

resource "aws_appautoscaling_target" "reporting_target" {
  service_namespace  = "ecs"
  resource_id        = "service/${var.ecs_cluster_name}/${aws_ecs_service.reporting-service.name}"
  scalable_dimension = "ecs:service:DesiredCount"
  role_arn           = "${var.ecs_scaling_role_arn}"
  min_capacity       = "${(var.desired_count - 1) < 1 ? 1 : (var.desired_count - 1)}"
  max_capacity       = "${var.desired_count * 2}"
}

resource "aws_appautoscaling_policy" "reporting_up" {
  name               = "${var.environment}_scale_up"
  service_namespace  = "ecs"
  resource_id        = "service/${var.ecs_cluster_name}/${aws_ecs_service.reporting-service.name}"
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
    "aws_appautoscaling_target.reporting_target",
    "aws_ecs_service.reporting-service",
  ]
}

resource "aws_appautoscaling_policy" "reporting_down" {
  name               = "${var.environment}_scale_down"
  service_namespace  = "ecs"
  resource_id        = "service/${var.ecs_cluster_name}/${aws_ecs_service.reporting-service.name}"
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
    "aws_appautoscaling_target.reporting_target",
    "aws_ecs_service.reporting-service",
  ]
}

/* metric used for auto scale */
resource "aws_cloudwatch_metric_alarm" "reporting_service_cpu_high" {
  alarm_name          = "SURS ${var.environment}_reporting_cpu_utilization_high"
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
    ServiceName = "${aws_ecs_service.reporting-service.name}"
  }

  alarm_actions = [
    "${aws_appautoscaling_policy.reporting_up.arn}",
  ]

  ok_actions = [
    "${aws_appautoscaling_policy.reporting_down.arn}",
  ]

  depends_on = ["aws_appautoscaling_policy.reporting_up", "aws_appautoscaling_policy.reporting_down", "aws_ecs_service.reporting-service"]
}
