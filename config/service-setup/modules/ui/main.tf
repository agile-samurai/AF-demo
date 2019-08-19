resource "aws_ecs_task_definition" "service" {
  family = "${var.container_family}"
  requires_compatibilities = [
  "FARGATE"]
  network_mode       = "awsvpc"
  cpu                = "${var.cpu}"
  memory             = "${var.memory}"
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
    "dependsOn": [
    {
      "containerName": "${var.server_container_family}",
      "condition": "HEALTHY"
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
     { "name": "SERVER_URL", "value": "${var.server_url}" }
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
  # depends_on = [
  #   "aws_alb_target_group.front_end_http","aws_alb_target_group.front_end_ssl",
  # "aws_alb.lb"]

  # Track the latest ACTIVE revision
  task_definition = "${aws_ecs_task_definition.service.family}:${max("${aws_ecs_task_definition.service.revision}", "${aws_ecs_task_definition.service.revision}")}"

  network_configuration {
    security_groups = [
    "${aws_security_group.lb.id}"]
    subnets = "${var.private_subnets}"
  }

  load_balancer {
    target_group_arn = "${aws_alb_target_group.front_end_https.id}"
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


# generate self signed certificate for load balancer
resource "tls_private_key" "example" {
  algorithm = "RSA"
}

resource "tls_self_signed_cert" "example" {
  key_algorithm   = "RSA"
  private_key_pem = "${tls_private_key.example.private_key_pem}"
  early_renewal_hours = 2

  subject {
    common_name  = "u.group"
    organization = "U Group"
  }

  validity_period_hours = 12

  allowed_uses = [
    "key_encipherment",
    "digital_signature",
    "server_auth",
  ]
   depends_on = ["tls_private_key.example"]

}

resource "aws_iam_server_certificate" "test_cert" {
  name_prefix      = "example-cert"
  certificate_body = "${tls_self_signed_cert.example.cert_pem}"
  private_key      = "${tls_private_key.example.private_key_pem}"

  lifecycle {
    create_before_destroy = true
  }
  depends_on = ["tls_self_signed_cert.example"]
}


resource "aws_alb_target_group" "front_end_https" {
  port = "${var.container_port}"
  protocol = "HTTP"
  vpc_id = "${var.vpc_id}"
  target_type = "ip"

  health_check {
    healthy_threshold = 2
    unhealthy_threshold = 10
    protocol = "HTTP"
    path = "/"
    interval = 32
    timeout = 30
    matcher = "${var.matcher_ports}"
  }
}

# resource "aws_alb_target_group" "front_end_http" {
#   port = "${var.container_port}"
#   protocol = "HTTP"
#   vpc_id = "${var.vpc_id}"
#   target_type = "ip"

#   health_check {
#     healthy_threshold = 2
#     unhealthy_threshold = 10
#     protocol = "HTTP"
#     path = "/"
#     interval = 32
#     timeout = 30
#     matcher = "${var.matcher_ports}"
#   }
# }

resource "aws_alb_listener" "front_end_https" {
  load_balancer_arn = "${aws_alb.lb.id}"
  port = "${var.loadbalancer_port_https}"
  protocol = "HTTPS"
  ssl_policy = "ELBSecurityPolicy-2016-08"  #predefined ssl  security policy  
  certificate_arn   = "${aws_iam_server_certificate.test_cert.arn}"

  default_action {
    target_group_arn = "${aws_alb_target_group.front_end_https.id}"
    type = "forward"
  }
  depends_on = ["aws_iam_server_certificate.test_cert"]
}

# resource "aws_alb_listener" "front_end_http" {
#   load_balancer_arn = "${aws_alb.lb.id}"
#   port = "${var.loadbalancer_port}"
#   protocol = "HTTP"

#   default_action {
#     target_group_arn = "${aws_alb_target_group.front_end_http.id}"
#     type = "forward"
#   }
# }




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
    protocol = "tcp"
    from_port = 443
    to_port = 8080
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    protocol = "tcp"
    from_port = 8080
    to_port = 8080
    cidr_blocks = ["0.0.0.0/0"]
  }


  egress {
    from_port = 0
    to_port = 0
    protocol = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# resource "aws_route53_record" "www" {
#   zone_id = "${var.zone_id}"
#   name = "${var.container_family}.${var.base_domain}"
#   type = "CNAME"
#   ttl = "300"
#   records = [
#     "${aws_alb.lb.dns_name}"]
# }


//resource "aws_route53_record" "cert_validation" {
//  zone_id = "${var.zone_id}"
//  name = "${aws_acm_certificate.cert.domain_validation_options.0.resource_record_name}"
//  type = "${aws_acm_certificate.cert.domain_validation_options.0.resource_record_type}"
//  records = [
//    "${aws_acm_certificate.cert.domain_validation_options.0.resource_record_value}"]
//  ttl = 60
//}
