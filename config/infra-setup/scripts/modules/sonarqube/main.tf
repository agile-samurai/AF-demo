resource "tls_private_key" "key" {
  algorithm = "RSA"
}

resource "aws_key_pair" "key" {
  key_name   = "${var.service_name}_${var.environment}_key"
  public_key = "${tls_private_key.key.public_key_openssh}"
}

// Load Template File
data "template_file" "user_data" {
  template = "${file("${path.module}/template/user_data.tpl")}"

  vars = {
    cluster_name = "${var.ecs_cluster_name}"
  }
}

//ECS Service Security Group Start
resource "aws_security_group" "allow_service" {
  name_prefix = "${var.environment}-${var.vpc_id}"
  description = "${var.sg_description}"
  vpc_id      = "${var.vpc_id}"

  ingress {
    from_port   = "${var.lb_target_group_port}"
    to_port     = "${var.lb_target_group_port}"
    protocol    = "${var.sg_protocol}"
    cidr_blocks = ["${var.sg_cidr_block}"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["${var.sg_cidr_block}"]
  }

  tags = {
    Name = "${var.sg_description} - ${var.environment}"
  }
}

//ECS Service Security Group End

//Load Balancer Start
resource "aws_lb" "lb" {
  name               = "${var.service_name}-${var.environment}-lb"
  subnets            = "${var.public-subnets}"
  load_balancer_type = "${var.load_balancer_type}"
  internal           = false
  idle_timeout       = 300
  security_groups    = ["${var.sg-allow-inbound}"]

  tags = {
    Name        = "${var.service_name}-${var.environment}-lb"
    Environment = "${var.environment}"
  }
}

resource "aws_lb_target_group" "lb_target" {
  name     = "${var.service_name}-${var.environment}-lb-target"
  port     = "${var.host_port}"
  protocol = "${var.lb_target_group_protocol}"
  vpc_id   = "${var.vpc_id}"

  health_check {
    healthy_threshold   = 5
    unhealthy_threshold = 10
    protocol            = "${var.lb_target_group_protocol}"
    path                = "${var.lb_health_check_path}"
    interval            = 120
    timeout             = 10
    matcher             = 200
  }

  tags = {
    Environment = "${var.environment}"
  }

  depends_on = ["aws_lb.lb"]
}

resource "aws_lb_listener" "http_listener" {
  load_balancer_arn = "${aws_lb.lb.arn}"
  port              = "${var.lb_listener_port}"
  protocol          = "${var.lb_listener_protocol}"

  default_action {
    target_group_arn = "${aws_lb_target_group.lb_target.arn}"
    type             = "forward"
  }

  depends_on = ["aws_lb_target_group.lb_target"]
}

//create log group
resource "aws_cloudwatch_log_group" "log_group" {
  name              = "${var.project}_${var.environment}_${var.service_name}"
  retention_in_days = 7

  tags = {
    Project     = "${var.project}"
    Environment = "${var.environment}"
    Service     = "${var.service_name}"
  }
}

// configure service task definition

data "aws_ecs_task_definition" "task_definition" {
  task_definition = "${aws_ecs_task_definition.task_definition.family}"
  depends_on      = ["aws_ecs_task_definition.task_definition"]
}

resource "aws_ecs_task_definition" "task_definition" {
  family = "${var.project}_${var.environment}_${var.service_name}"

  container_definitions = <<DEFINITION
  [
  {
    "family": "${var.project}_${var.environment}_${var.service_name}",
    "name": "${var.project}_${var.environment}_${var.service_name}",
    "image": "${var.sonarqube_image}",
    "environment":[
      { "name" : "SONARQUBE_JDBC_USERNAME", "value" : "${var.sonarqube_rds_username}" },
      { "name" : "SONARQUBE_JDBC_PASSWORD", "value" : "${var.sonarqube_rds_password}" },
      { "name" : "SONARQUBE_JDBC_URL", "value" : "jdbc:mysql://${aws_db_instance.sonarqube_rds.address}:3306/${var.sonarqube_rds_dbname}?useConfigs=maxPerformance&useSSL=false&useUnicode=true&characterEncoding=utf8&rewriteBatchedStatements=true"}
    ],
    "cpu": ${var.cpu},
    "memory": ${var.memory},
    "essential": true,
    "command": [],
    "logConfiguration": {
      "logDriver": "awslogs",
      "options": {
        "awslogs-group": "${aws_cloudwatch_log_group.log_group.name}",
        "awslogs-region": "${var.region}",
        "awslogs-stream-prefix": "${var.project}_${var.environment}_${var.service_name}"
      }
    },
     "portMappings": [
      {
          "containerPort": ${var.host_port},
          "hostPort": ${var.host_port}
      }
     ]
  }
  ]
DEFINITION

  execution_role_arn = "${var.ecs_task_execution_role_arn}"
  task_role_arn      = "${var.ecs_task_execution_role_arn}"
}

// ecs service

resource "aws_ecs_service" "service" {
  name          = "${var.project}_${var.environment}_${var.service_name}"
  desired_count = "${var.service_desired_count}"
  cluster       = "${var.ecs_cluster_name}"
  depends_on    = ["aws_ecs_task_definition.task_definition", "aws_lb_listener.http_listener"]

  task_definition = "${aws_ecs_task_definition.task_definition.family}:${max("${aws_ecs_task_definition.task_definition.revision}", "${aws_ecs_task_definition.task_definition.revision}")}"
  iam_role        = "${var.ecs_lb_role_arn}"

  load_balancer {
    target_group_arn = "${aws_lb_target_group.lb_target.arn}"
    container_name   = "${var.project}_${var.environment}_${var.service_name}"
    container_port   = "${var.host_port}"
  }
}

//ecs autoscale start

resource "aws_appautoscaling_target" "app_scaling_target" {
  service_namespace  = "ecs"
  resource_id        = "service/${var.ecs_cluster_name}/${aws_ecs_service.service.name}"
  scalable_dimension = "ecs:service:DesiredCount"
  role_arn           = "${var.ecs_lb_role_arn}"
  min_capacity       = "${var.service_desired_count}"
  max_capacity       = "${var.service_desired_count}"
}

resource "aws_appautoscaling_policy" "app_service_up" {
  name               = "${var.environment}_${var.service_name}_scale_up"
  service_namespace  = "ecs"
  resource_id        = "service/${var.ecs_cluster_name}/${aws_ecs_service.service.name}"
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
    "aws_appautoscaling_target.app_scaling_target",
    "aws_ecs_service.service",
  ]
}

resource "aws_appautoscaling_policy" "scale_service_down" {
  name               = "${var.environment}_${var.service_name}_scale_down"
  service_namespace  = "ecs"
  resource_id        = "service/${var.ecs_cluster_name}/${aws_ecs_service.service.name}"
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
    "aws_appautoscaling_target.app_scaling_target",
    "aws_ecs_service.service",
  ]
}

/* metric used for auto scale */
resource "aws_cloudwatch_metric_alarm" "service_cpu_high" {
  alarm_name          = "${var.environment}_${var.service_name}_cpu_utilization_high"
  comparison_operator = "GreaterThanOrEqualToThreshold"
  evaluation_periods  = "2"
  metric_name         = "MemoryUtilization"
  namespace           = "AWS/ECS"
  period              = "60"
  statistic           = "Maximum"
  threshold           = "85"
  unit                = "Percent"

  dimensions = {
    ClusterName = "${var.ecs_cluster_name}"
    ServiceName = "${aws_ecs_service.service.name}"
  }

  alarm_actions = [
    "${aws_appautoscaling_policy.app_service_up.arn}",
  ]

  ok_actions = [
    "${aws_appautoscaling_policy.scale_service_down.arn}",
  ]

  depends_on = ["aws_appautoscaling_policy.app_service_up", "aws_appautoscaling_policy.scale_service_down", "aws_ecs_service.service"]
}

//ecs autoscale end

//create launch configuration
resource "aws_launch_configuration" "asg_conf" {
  name_prefix          = "${var.environment}_${var.service_name}_asg_conf"
  instance_type        = "${var.asg_instance_type}"
  image_id             = "${var.ami}"
  iam_instance_profile = "${var.iam_instance_profile}"

  security_groups = ["${var.sg-allow-inbound}", "${var.sg-allow-ssh}", "${aws_security_group.allow_service.id}"]
  user_data       = "${data.template_file.user_data.rendered}"

  key_name = "${aws_key_pair.key.key_name}"
}

//autoscaling group start
resource "aws_autoscaling_group" "service_asg" {
  name = "${var.environment}_${aws_launch_configuration.asg_conf.name}_asg"

  vpc_zone_identifier = "${var.private-subnets}"

  min_size = "${var.service_desired_count}"
  max_size = "${var.service_desired_count}"

  launch_configuration = "${aws_launch_configuration.asg_conf.name}"
  health_check_type    = "EC2"

  target_group_arns = ["${aws_lb_target_group.lb_target.arn}"]

  tag {
    key                 = "Name"
    value               = "${var.ecs_cluster_name}_instance"
    propagate_at_launch = true
  }

  depends_on = ["aws_launch_configuration.asg_conf"]
}

resource "aws_autoscaling_policy" "autoscale_policy" {
  name                   = "${var.environment}_${var.service_name}_cpu60"
  adjustment_type        = "ChangeInCapacity"
  autoscaling_group_name = "${aws_autoscaling_group.service_asg.name}"
  policy_type            = "TargetTrackingScaling"

  target_tracking_configuration {
    predefined_metric_specification {
      predefined_metric_type = "ASGAverageCPUUtilization"
    }

    target_value = 60.0
  }

  depends_on = ["aws_autoscaling_group.service_asg"]
}

resource "aws_security_group" "sonarqube_rds_sg" {
  vpc_id      = "${var.vpc_id}"
  name_prefix = "${var.project}_${var.environment}_${var.service_name}_rds_sg"
  description = "Allow ${var.service_name} DB port traffic within cluster"

  ingress {
    from_port   = "${var.sonarqube_db_port}"
    to_port     = "${var.sonarqube_db_port}"
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${var.project}_${var.environment}_${var.service_name}_rds_sg"
  }
}

resource "aws_db_subnet_group" "sonarqube_db_subnet_group" {
  name        = "${var.project}_${var.environment}_${var.service_name}_db_subnet_grp" //externalize as variable
  description = "SonarQube RDS DB subnets"                                            //externalize as variable
  subnet_ids  = "${var.private-subnets}"
}

resource "aws_db_instance" "sonarqube_rds" {
  identifier             = "${var.project}-${var.environment}-${var.sonarqube_rds_dbname}"
  allocated_storage      = "${var.sonarqube_rds_storage}"
  storage_type           = "${var.sonarqube_rds_storage_type}"
  instance_class         = "${var.sonarqube_db_instance_class}"
  name                   = "${var.sonarqube_rds_dbname}"
  username               = "${var.sonarqube_rds_username}"
  password               = "${var.sonarqube_rds_password}"
  engine                 = "${var.sonarqube_rds_engine}"
  engine_version         = "${lookup(var.sonarqube_rds_engine_version, var.sonarqube_rds_engine)}"
  vpc_security_group_ids = ["${aws_security_group.sonarqube_rds_sg.id}"]
  db_subnet_group_name   = "${aws_db_subnet_group.sonarqube_db_subnet_group.id}"
  publicly_accessible    = false //externalize as variable
  skip_final_snapshot    = true  //externalize as variable
  depends_on             = ["aws_db_subnet_group.sonarqube_db_subnet_group", "aws_security_group.sonarqube_rds_sg"]
}
