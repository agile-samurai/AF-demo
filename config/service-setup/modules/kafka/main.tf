data aws_vpc sbir-one-vpc {
  id = "${var.vpc_id}"
}

data "aws_ssm_parameter" "ecs-ami" {
  name = "/aws/service/ecs/optimized-ami/amazon-linux/recommended"
}

resource "aws_iam_instance_profile" "ecs" {
  name = "kafka-${var.environment}_ecs-instance-profile"
  role = "${var.ecs_instance_role_name}"
}

locals {
  ecs_optimized_ami = "${replace(element(split(":", element(split(",", data.aws_ssm_parameter.ecs-ami.value), 2)), 1), "\"", "" )}"
}

data "template_file" "user_data" {
  template = "${file("${path.module}/templates/user_data.sh")}"

  vars = {
    cluster_name = "${var.ecs_cluster_name}"
  }
}

/*====
Auto-Scaling Group stuff
======*/

resource "tls_private_key" "key" {
  algorithm = "RSA"
}

resource "aws_key_pair" "key" {
  key_name   = "kafka_${var.environment}_key"
  public_key = "${tls_private_key.key.public_key_openssh}"
}

resource "aws_security_group" "allow_zookeeper" {
  name_prefix = "sbir-one-${var.vpc_id}-"
  description = "Allow traffic and zookeeper within cluster"
  vpc_id      = "${var.vpc_id}"

  ingress {
    from_port   = 2181
    to_port     = 2181
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 2181
    to_port     = 2181
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "Allow Zookeeper cluster - ${var.environment}"
  }

  depends_on = ["data.aws_vpc.sbir-one-vpc"]
}

resource "aws_security_group" "allow_kafka" {
  name_prefix = "sbir-one-${var.vpc_id}-"
  description = "Allow traffic and zookeeper within cluster"
  vpc_id      = "${var.vpc_id}"

  ingress {
    from_port   = 9092
    to_port     = 9092
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 9092
    to_port     = 9092
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "Allow Kafka cluster - ${var.environment}"
  }

  depends_on = ["data.aws_vpc.sbir-one-vpc"]
}

# data "aws_instance" "kafka" {
#   filter {
#     name   = "tag:Name"
#     values = ["kafka-${var.environment}-ecs-asg"]
#   }
#
#   depends_on = ["aws_autoscaling_group.kafka_ecs_cluster_asg"]
# }
#
# resource "aws_launch_configuration" "ecs_cluster-asg-conf" {
#   name_prefix          = "kafka_${var.environment}-asg_conf"
#   instance_type        = "t3.micro"
#   image_id             = "${local.ecs_optimized_ami}"
#   iam_instance_profile = "${aws_iam_instance_profile.ecs.id}"
#
#   security_groups = [
#     "${var.sg-allow-cluster}",
#     "${aws_security_group.allow_zookeeper.id}",
#     "${aws_security_group.allow_kafka.id}",
#     "${aws_security_group.allow_schema-registry.id}",
#   ]
#
#   user_data = "${data.template_file.user_data.rendered}"
#   key_name  = "${aws_key_pair.key.key_name}"
#
#   root_block_device {
#     volume_type = "gp2"
#     volume_size = 250
#   }
#
#   lifecycle {
#     create_before_destroy = true
#   }
#
#   depends_on = [
#     "aws_key_pair.key",
#     "aws_iam_instance_profile.ecs",
#     "aws_security_group.allow_zookeeper",
#     "aws_security_group.allow_kafka",
#     "aws_security_group.allow_schema-registry",
#     "data.template_file.user_data",
#   ]
# }
#
# resource "aws_autoscaling_group" "kafka_ecs_cluster_asg" {
#   name = "kafka-${aws_launch_configuration.ecs_cluster-asg-conf.name}-asg"
#
#   vpc_zone_identifier = ["${var.private_subnets}"]
#
#   min_size = 1
#   max_size = "${(var.zookeeper_svc_count + var.broker_svc_count + var.schema_registry_svc_count) * 2}"
#
#   launch_configuration = "${aws_launch_configuration.ecs_cluster-asg-conf.name}"
#   health_check_type    = "EC2"
#
#   tag {
#     key                 = "Name"
#     value               = "kafka-${var.environment}-ecs-asg"
#     propagate_at_launch = true
#   }
#
#   depends_on = ["aws_launch_configuration.ecs_cluster-asg-conf"]
# }
#
# resource "aws_autoscaling_policy" "kafka_autoscale_policy" {
#   name                   = "kafka-${var.environment}-cpu60"
#   adjustment_type        = "ChangeInCapacity"
#   autoscaling_group_name = "${aws_autoscaling_group.kafka_ecs_cluster_asg.name}"
#   policy_type            = "TargetTrackingScaling"
#
#   target_tracking_configuration {
#     predefined_metric_specification {
#       predefined_metric_type = "ASGAverageCPUUtilization"
#     }
#
#     target_value = 60.0
#   }
#
#   depends_on = ["aws_autoscaling_group.kafka_ecs_cluster_asg"]
# }

/*
resource "aws_elb" "kafka" {
  name = "${var.environment}-kafka-elb"

  subnets                   = ["${var.public_subnets}"]
  connection_draining       = true
  cross_zone_load_balancing = true

  listener {
    lb_port           = 9092
    lb_protocol       = "tcp"
    instance_port     = 9092
    instance_protocol = "tcp"
  }

  listener {
    lb_port           = 2181
    lb_protocol       = "tcp"
    instance_port     = 2181
    instance_protocol = "tcp"
  }

  health_check {
    healthy_threshold   = 2
    unhealthy_threshold = 2
    timeout             = 3
    target              = "TCP:9092"
    interval            = 10
  }

  security_groups = [
    "${data.aws_security_group.allow_inbound.id}",
    "${var.sg-allow-cluster}",
    "${var.sg-allow-ssh}",
    "${aws_security_group.allow_zookeeper.id}",
    "${aws_security_group.allow_kafka.id}"
  ]

  tags {
    Name = "kafka-${var.environment}"
  }
}

resource "aws_route53_record" "kafka" {
  name    = "kafka-${var.environment}"
  zone_id = "${var.zone_id}"
  type    = "A"

  alias {
    name                   = "${aws_elb.kafka.dns_name}"
    zone_id                = "${aws_elb.kafka.zone_id}"
    evaluate_target_health = true
  }
}
*/

resource "aws_service_discovery_private_dns_namespace" "private_ns" {
  name        = "${var.private_dns}"
  description = "SbirOne private dns namespace"
  vpc         = "${var.vpc_id}"
}

/*
resource "aws_service_discovery_service" "private_discovery_service" {
  name = "private_discovery_service"
  dns_config {
    namespace_id = "${aws_service_discovery_private_dns_namespace.private_ns.id}"
    dns_records {
      ttl = 10
      type = "SRV"
    }
    routing_policy = "MULTIVALUE"
  }

  health_check_custom_config {
    failure_threshold = 1
  }
}
*/

resource "aws_service_discovery_service" "zookeeper_service" {
  name = "${var.environment}-zookeeper"

  dns_config {
    namespace_id = "${aws_service_discovery_private_dns_namespace.private_ns.id}"

    dns_records {
      ttl  = 10
      type = "SRV"
    }

    routing_policy = "MULTIVALUE"
  }

  health_check_custom_config {
    failure_threshold = 1
  }
}

resource "aws_service_discovery_service" "kafka_service" {
  name = "${var.environment}-kafka-broker"

  dns_config {
    namespace_id = "${aws_service_discovery_private_dns_namespace.private_ns.id}"

    dns_records {
      ttl  = 10
      type = "SRV"
    }

    routing_policy = "MULTIVALUE"
  }

  health_check_custom_config {
    failure_threshold = 1
  }
}


module "zookeeper" {
  source                      = "./zookeeper"
  discovery_service_arn       = "${aws_service_discovery_service.zookeeper_service.arn}"
  private_dns                 = "${aws_service_discovery_private_dns_namespace.private_ns.name}"
  service_desired_count       = "${var.zookeeper_svc_count}"
  environment                 = "${var.environment}"
  region                      = "${var.region}"
  ecs_service_role_arn        = "${var.ecs_service_role_arn}"
  ecs_task_execution_role_arn = "${var.ecs_task_execution_role_arn}"
  ecs_cluster_id              = "${var.ecs_cluster_id}"
  ecs_cluster_name            = "${var.ecs_cluster_name}"
}

module "kafka-broker" {
  source                      = "./kafka-broker"
  discovery_service_arn       = "${aws_service_discovery_service.kafka_service.arn}"
  private_dns                 = "${aws_service_discovery_private_dns_namespace.private_ns.name}"
  service_desired_count       = "${var.broker_svc_count}"
  environment                 = "${var.environment}"
  region                      = "${var.region}"
  ecs_service_role_arn        = "${var.ecs_service_role_arn}"
  ecs_task_execution_role_arn = "${var.ecs_task_execution_role_arn}"
  ecs_cluster_id              = "${var.ecs_cluster_id}"
  ecs_cluster_name            = "${var.ecs_cluster_name}"
  kafka_internal_ip           = "${var.ecs_instance_ip}"
}
