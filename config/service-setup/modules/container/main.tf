resource "aws_ecs_task_definition" "service" {
  family = "${var.container_family}"
  requires_compatibilities = [
    "FARGATE"]
  network_mode = "awsvpc"
  cpu = "${var.cpu}"
  memory = "${var.memory}"
  execution_role_arn = "${var.execution_role_arn}"

  container_definitions = <<DEFINITION
[
 {
   "cpu": ${var.cpu},
   "memory": ${var.memory},
   "name": "${var.container_family}",
   "image": "${var.docker_image}",
   "networkMode": "awsvpc",
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
     { "name": "SPRING_DATASOURCE_URL", "value": "jdbc:postgresql://${var.postgres_url}:5432/postgres" },
     { "name": "SPRING_DATASOURCE_USERNAME", "value": "${var.postgres_username}" },
     { "name": "SPRING_DATASOURCE_PASSWORD", "value": "${var.postgres_password}" },

     { "name": "ENVIRONMENT_AUTHHOST_INTERNAL", "value": "${var.ENVIRONMENT_AUTHHOST_INTERNAL}" },
     { "name": "SPRING_PROFILES_ACTIVE", "value": "dev" },
     { "name": "SPRING_DATA_MONGODB_AUTHENTICATION_DATABASE", "value": "admin" },
     { "name": "KAFKA_INTERNAL_IP", "value" :"${var.KAFKA_INTERNAL_IP}"},
     { "name": "PERSISTENCE_MONGO_URL", "value" :"${var.PERSISTENCE_MONGO_URL}"},

     { "name": "DATA_SCIENCE_SERVICE_BASEURL", "value" :"http://${var.data_science_url}"},
     { "name": "SPRING_DATA_JEST_URI", "value" :"http://${var.es_endpoint}"},

     { "name": "SPRING_DATA_MONGODB_HOST", "value" :"${var.SPRING_DATA_MONGODB_HOST}"},
     { "name": "SPRING_DATA_MONGODB_USERNAME", "value" :"${var.SPRING_DATA_MONGODB_USERNAME}"},
     { "name": "SPRING_DATA_MONGODB_PASSWORD", "value" :"${var.SPRING_DATA_MONGODB_PASSWORD}"},
     { "name": "SPRING_DATA_MONGODB_PORT", "value" :"${var.SPRING_DATA_MONGODB_PORT}"}
   ]
 }
]
DEFINITION
}

resource "aws_ecs_service" "service" {
  name = "${var.container_family}"
  cluster = "${var.cluster_id}"
  desired_count = "${var.instance_count}"

  launch_type = "FARGATE"
  depends_on = [
    "aws_alb_target_group.front_end",
    "aws_alb.lb"]

  # Track the latest ACTIVE revision
  task_definition = "${aws_ecs_task_definition.service.family}:${max("${aws_ecs_task_definition.service.revision}", "${aws_ecs_task_definition.service.revision}")}"

  network_configuration {
    security_groups = [
      "${aws_security_group.lb.id}"]
    subnets = "${var.private_subnets}"
  }

  load_balancer {
    target_group_arn = "${aws_alb_target_group.front_end.id}"
    container_name = "${var.container_family}"
    container_port = "${var.container_port}"
  }
}

resource "aws_alb" "lb" {
  security_groups = ["${aws_security_group.lb.id}"]
  subnets = "${var.public_subnets}"
  enable_deletion_protection = false
  idle_timeout = "${var.timeout}"
  name = "${var.container_family}-lb"

  tags = {
    Environment = "${terraform.workspace}"
  }
}

resource "aws_alb_target_group" "front_end" {
  port = "${var.container_port}"
  protocol = "HTTP"
  vpc_id = "${var.vpc_id}"
  target_type = "ip"

  health_check {
    healthy_threshold = 2
    unhealthy_threshold = 10
    protocol = "HTTP"
    path = "/actuator/health"
    interval = 32
    timeout = 30
    matcher = "${var.matcher_ports}"
  }
}

resource "aws_alb_listener" "front_end" {
  load_balancer_arn = "${aws_alb.lb.id}"
  port = "${var.loadbalancer_port}"
  protocol = "HTTP"

    default_action {
      target_group_arn = "${aws_alb_target_group.front_end.id}"
      type = "forward"
    }
}
//
//resource "aws_alb_listener" "front_end_https" {
//  load_balancer_arn = "${aws_alb.lb.id}"
//  port = "${var.loadbalancer_port_https}"
//  protocol = "HTTPS"
//  ssl_policy = "ELBSecurityPolicy-2016-08"
//  certificate_arn = "${aws_acm_certificate.cert.arn}"
//  default_action {
//    target_group_arn = "${aws_alb_target_group.front_end.id}"
//    type = "forward"
//  }
//}
//resource "aws_acm_certificate" "cert" {
//  domain_name = "${var.container_family}.${var.base_domain}"
//  validation_method = "DNS"
//  lifecycle {
//    create_before_destroy = true
//  }
//}
//resource "aws_acm_certificate_validation" "cert" {
//  certificate_arn = "${aws_acm_certificate.cert.arn}"
//  validation_record_fqdns = [
//    "${aws_route53_record.cert_validation.fqdn}"]
//}

# ALB Security group
# This is the group you need to edit if you want to restrict access to your application
resource "aws_security_group" "lb" {
  description = "controls access to the ALB"
  vpc_id = "${var.vpc_id}"

  ingress {
    protocol = "tcp"
    from_port = 80
    to_port = 80
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    protocol    = "tcp"
    from_port   = 443
    to_port     = 8080
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    protocol    = "tcp"
    from_port   = 8080
    to_port     = 8080
    cidr_blocks = ["0.0.0.0/0"]
  }


  egress {
    from_port = 0
    to_port = 0
    protocol = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_route53_record" "www" {
  zone_id = "${var.zone_id}"
  name = "${var.container_family}.${var.base_domain}"
  type = "CNAME"
  ttl = "300"
  records = [
    "${aws_alb.lb.dns_name}"]
}


//resource "aws_route53_record" "cert_validation" {
//  zone_id = "${var.zone_id}"
//  name = "${aws_acm_certificate.cert.domain_validation_options.0.resource_record_name}"
//  type = "${aws_acm_certificate.cert.domain_validation_options.0.resource_record_type}"
//  records = [
//    "${aws_acm_certificate.cert.domain_validation_options.0.resource_record_value}"]
//  ttl = 60
//}
