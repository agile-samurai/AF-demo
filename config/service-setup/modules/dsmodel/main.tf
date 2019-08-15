resource "aws_ecs_task_definition" "container" {
  family                   = "${var.container_family}"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  execution_role_arn       = "${var.execution_role_arn}"
  cpu                      = "${var.cpu}"
  memory                   = "${var.memory}"

  container_definitions = <<DEFINITION
[
  {
    "cpu": ${var.cpu},
    "memory": ${var.memory},
    "name": "${var.container_family}",
    "image": "${var.docker_image}",
    "networkMode": "awsvpc",
    "healthCheck": {

        "startPeriod": 300,
        "command": [ "CMD-SHELL", "curl -f http://localhost:8080/metrics  || exit 1" ],
        "interval": 40,
        "timeout": 10,
        "retries": 10
    },
    "portMappings": [
      {
        "containerPort": ${var.container_port},
        "hostPort": ${var.container_port}
      }
    ],
    "logConfiguration": {
      "logDriver": "awslogs",
      "options": {
        "awslogs-group": "${var.cloud_watch_log_group_name}",
        "awslogs-region": "${var.region}",
        "awslogs-stream-prefix": "logs"
      }
    },
    "environment" : [
      { "name": "AWS_SECRET_ACCESS_KEY", "value" :"${var.access_secret}" },
      { "name": "AWS_ACCOUNT_ID", "value" :"${var.account_id}" },
      { "name": "AWS_ACCESS_KEY_ID", "value" :"${var.access_key}" },
      { "name": "ENVIRONMENT", "value": "${terraform.workspace}" },
      { "name": "AWS_DEFAULT_REGION", "value": "${var.region}" }
    ]
  }
]
DEFINITION
}

resource "aws_security_group" "service" {
  name_prefix = "${terraform.workspace}-${var.container_family}"
  vpc_id      = "${var.vpc_id}"

  tags = {
    Environment = "${terraform.workspace}"
    Role        = "${var.container_family}"
  }
}

resource "aws_security_group_rule" "from_lb_to_service" {
  security_group_id        = "${aws_security_group.service.id}"
  type                     = "ingress"
  protocol                 = "TCP"
  from_port                = "${var.container_port}"
  to_port                  = "${var.container_port}"
  source_security_group_id = "${aws_security_group.lb.id}"
}

resource "aws_security_group_rule" "allow_all_outbound_to_anywhere" {
  security_group_id = "${aws_security_group.service.id}"
  type              = "egress"
  protocol          = "-1"
  from_port         = 0
  to_port           = 0
  cidr_blocks       = ["0.0.0.0/0"]
}

resource "aws_ecs_service" "service" {
  name          = "${var.container_family}"
  cluster       = "${var.cluster_id}"
  desired_count = "${var.instance_count}"

  launch_type = "FARGATE"

  # Track the latest ACTIVE revision
  task_definition = "${aws_ecs_task_definition.container.family}:${max("${aws_ecs_task_definition.container.revision}", "${aws_ecs_task_definition.container.revision}")}"

  network_configuration {
    security_groups = ["${aws_security_group.service.id}"]
    subnets         = "${var.private_subnets}"
  }

  load_balancer {
    target_group_arn = "${aws_alb_target_group.container.id}"
    container_name   = "${var.container_family}"
    container_port   = "${var.container_port}"
  }

  depends_on = ["aws_ecs_task_definition.container", "aws_alb_target_group.container", "aws_alb.lb"]
}

resource "aws_alb" "lb" {
  name                       = "${terraform.workspace}-${var.container_family}"
  security_groups            = ["${aws_security_group.lb.id}"]
  subnets                    = "${var.public_subnets}"
  enable_deletion_protection = false
  idle_timeout               = "${var.timeout}"

  tags = {
    Environment = "${terraform.workspace}"
  }
}

resource "aws_alb_target_group" "container" {
  name        = "${terraform.workspace}-${var.container_family}"
  port        = "${var.container_port}"
  protocol    = "HTTP"
  vpc_id      = "${var.vpc_id}"
  target_type = "ip"

  health_check {
    path                = "${var.health_check_path}"
    matcher             = "${var.matcher_ports}"
    interval            = "${var.interval}"
    timeout             = "${var.timeout}"
    healthy_threshold   = "${var.healthy_threshold}"
    unhealthy_threshold = "${var.unhealthy_threshold}"
  }
}

# resource "aws_alb_target_group" "container_https" {
#   name        = "${var.project}-${terraform.workspace}-${var.container_family}-s"
#   port        = "${var.container_port}"
#   protocol    = "HTTPS"
#   vpc_id      = "${var.vpc_id}"
#   target_type = "ip"

#   health_check {
#     path                = "${var.health_check_path}"
#     matcher             = "${var.matcher_ports}"
#     interval            = "${var.interval}"
#     timeout             = "${var.timeout}"
#     healthy_threshold   = "${var.healthy_threshold}"
#     unhealthy_threshold = "${var.unhealthy_threshold}"
#   }
# }

resource "aws_alb_listener" "front_end" {
  load_balancer_arn = "${aws_alb.lb.id}"
  port              = "${var.loadbalancer_port}"
  protocol          = "HTTP"

  # ssl_policy        = "ELBSecurityPolicy-2016-08"
  # certificate_arn   = "${var.certificate_arn}"

  default_action {
    target_group_arn = "${aws_alb_target_group.container.id}"
    type             = "forward"
  }
}

# resource "aws_alb_listener" "front_end_https" {
#   load_balancer_arn = "${aws_alb.lb.id}"
#   port              = "${var.loadbalancer_port_https}"
#   protocol          = "HTTPS"
#   ssl_policy        = "ELBSecurityPolicy-2016-08"
#   certificate_arn   = "${data.aws_acm_certificate.acm_cert.arn}"

#   default_action {
#     target_group_arn = "${aws_alb_target_group.container_https.id}"
#     type             = "forward"
#   }
# }

# ALB Security group
# This is the group you need to edit if you want to restrict access to your application
resource "aws_security_group" "lb" {
  description = "controls access to the ALB"
  vpc_id      = "${var.vpc_id}"

  ingress {
    protocol    = "tcp"
    from_port   = "${var.loadbalancer_port}"
    to_port     = "${var.loadbalancer_port}"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_route53_record" "container" {
  zone_id = "${var.zone_id}"
  name    = "${var.container_family}-${terraform.workspace}.${var.base_domain}"
  type    = "CNAME"
  ttl     = "300"
  records = ["${aws_alb.lb.dns_name}"]
}
