//data "aws_route53_zone" "sbir-one" {
//  name = "sbir-one.bytecubed.io"
//vpc_id = "${data.aws_vpc.sbir-one-vpc.id}"
//}

data "aws_ecs_cluster" "main" {
  cluster_name = "${var.environment}-cluster"
}

data "aws_security_group" "allow_ssh" {
  tags {
    Name = "SBIR ONE allow ssh - ${var.environment}"
  }
}

data "aws_security_group" "allow_cluster" {
  tags {
    Name = "SBIR-ONE allow cluster - ${var.environment}"
  }
}

data "aws_ssm_parameter" "ecs-ami" {
  name = "/aws/service/ecs/optimized-ami/amazon-linux-2/recommended"
}

locals {
  key     = "${var.reporting_key}"
  ecs_ami = "${replace(element(split(":", element(split(",", data.aws_ssm_parameter.ecs-ami.value), 2)), 1), "\"", "" )}"
}

data "template_file" "user_data" {
  template = "${file("${path.module}/templates/user_data.sh")}"

  vars {
    cluster_name = "${data.aws_ecs_cluster.main.cluster_name}"
    environment  = "${var.environment}"
  }
}

data aws_iam_role ecs_service_role {
  name = "${var.environment}-ecs_service_role"
}

data aws_iam_role ecs_execution_role {
  name = "${var.environment}_ecs_task_execution_role"
}

data aws_iam_role ecs_autoscale_role {
  name = "${var.environment}_ecs_autoscale_role"
}

data aws_iam_role ecs_instance_role {
  name = "${var.environment}_ecs"
}

resource "aws_iam_instance_profile" "ecs" {
  name = "surs-${var.environment}_ecs-instance-profile"
  role = "${data.aws_iam_role.ecs_instance_role.name}"
}

module "reporting" {
  source                      = "../reporting"
  vpc-id                      = "${data.aws_vpc.sbir-one-vpc.id}"
  desired_count               = "${var.reporting_count}"
  environment                 = "${var.environment}"
  tag                         = "${var.tag}"
  region                      = "${var.region}"
  ecs_service_role_arn        = "${data.aws_iam_role.ecs_service_role.arn}"
  ecs_task_execution_role_arn = "${data.aws_iam_role.ecs_execution_role.arn}"
  route53_zone_id             = "${var.zone_id}"
  ecs_cluster_id              = "${data.aws_ecs_cluster.main.id}"
  ecs_cluster_name            = "${data.aws_ecs_cluster.main.cluster_name}"
  security_groups             = ["${data.aws_security_group.allow_cluster.id}", "${data.aws_security_group.allow_ssh.id}"]

  secret_key            = "${var.secret_key}"
  access_key            = "${var.access_key}"
  ecs_scaling_role_arn  = "${data.aws_iam_role.ecs_autoscale_role.arn}"
  mongo_pass            = "${var.mongo_pass}"
  auth_host_internal_ip = "${data.aws_instance.sso.private_ip}"
  kafka_internal_ip     = "${data.aws_instance.kafka.private_ip}"
}

module "index_svc" {
  source                      = "../index-svc"
  vpc-id                      = "${data.aws_vpc.sbir-one-vpc.id}"
  desired_count               = "${var.index_svc_count}"
  environment                 = "${var.environment}"
  tag                         = "${var.tag}"
  region                      = "${var.region}"
  ecs_service_role_arn        = "${data.aws_iam_role.ecs_service_role.arn}"
  ecs_task_execution_role_arn = "${data.aws_iam_role.ecs_execution_role.arn}"
  route53_zone_id             = "${var.zone_id}"
  ecs_cluster_id              = "${data.aws_ecs_cluster.main.id}"
  ecs_cluster_name            = "${data.aws_ecs_cluster.main.cluster_name}"
  security_groups             = ["${data.aws_security_group.allow_cluster.id}", "${data.aws_security_group.allow_ssh.id}"]
  ecs_scaling_role_arn        = "${data.aws_iam_role.ecs_autoscale_role.arn}"
  db_pass                     = "${var.db_pass}"
  admin_pass                  = "${var.admin_pass}"
  mongo_pass                  = "${var.mongo_pass}"
  kafka_internal_ip           = "${data.aws_instance.kafka.private_ip}"
}

/*====
Auto-Scaling Group stuff
======*/

data aws_subnet_ids private_subnet_ids {
  vpc_id = "${data.aws_vpc.sbir-one-vpc.id}"

  tags {
    Name = "${var.environment} VPC-private-us-east-1*"
  }

  depends_on = ["data.aws_vpc.sbir-one-vpc"]
}

data "aws_instance" "bastion" {
  filter {
    name   = "tag:Name"
    values = ["${var.environment} bastion"]
  }
}

data "aws_instance" "sso" {
  filter {
    name   = "tag:Name"
    values = ["sso-${var.environment}-ecs-asg"]
  }
}

data "aws_instance" "kafka" {
  filter {
    name   = "tag:Name"
    values = ["kafka-${var.environment}-ecs-asg"]
  }
}

resource "aws_security_group" "allow_admin_bastion" {
  name_prefix = "${data.aws_vpc.sbir-one-vpc.id}-"
  description = "Allow admin traffic for Bytecubed personel to bastion instance"
  vpc_id      = "${data.aws_vpc.sbir-one-vpc.id}"

  ingress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["${data.aws_instance.bastion.private_ip}/32"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["${data.aws_instance.bastion.private_ip}/32"]
  }

  tags {
    Name = "SBIR ONE allow admin for ${var.environment} bastion"
  }

  depends_on = ["data.aws_vpc.sbir-one-vpc", "data.aws_instance.bastion"]
}

resource "aws_key_pair" "key" {
  key_name   = "surs_${var.environment}_key"
  public_key = "${local.key}"
}

resource "aws_launch_configuration" "ecs_cluster-asg-conf" {
  instance_type        = "t2.medium"
  image_id             = "${local.ecs_ami}"
  iam_instance_profile = "${aws_iam_instance_profile.ecs.id}"

  security_groups = ["${data.aws_security_group.allow_cluster.id}", "${data.aws_security_group.allow_ssh.id}", "${aws_security_group.allow_admin_bastion.id}"]

  user_data = "${data.template_file.user_data.rendered}"
  key_name  = "${aws_key_pair.key.key_name}"

  lifecycle {
    create_before_destroy = true
  }

  depends_on = [
    "aws_key_pair.key",
    "aws_iam_instance_profile.ecs",
    "data.aws_security_group.allow_cluster",
    "data.aws_security_group.allow_ssh",
    "data.template_file.user_data",
  ]
}

resource "aws_autoscaling_group" "ecs_cluster_asg" {
  name = "${aws_launch_configuration.ecs_cluster-asg-conf.name}-asg"

  vpc_zone_identifier = ["${data.aws_subnet_ids.private_subnet_ids.ids}"]

  min_size = 2
  max_size = 2

  launch_configuration = "${aws_launch_configuration.ecs_cluster-asg-conf.name}"
  health_check_type    = "EC2"

  target_group_arns = ["${module.reporting.target_group_arn}", "${module.index_svc.target_group_arn}"]

  tag {
    key                 = "Name"
    value               = "surs-${var.environment}-ecs-asg"
    propagate_at_launch = true
  }

  depends_on = ["aws_launch_configuration.ecs_cluster-asg-conf", "data.aws_subnet_ids.private_subnet_ids"]
}

resource "aws_autoscaling_policy" "surs_autoscale_policy" {
  name                   = "surs-${var.environment}-cpu60"
  adjustment_type        = "ChangeInCapacity"
  autoscaling_group_name = "${aws_autoscaling_group.ecs_cluster_asg.name}"
  policy_type            = "TargetTrackingScaling"

  target_tracking_configuration {
    predefined_metric_specification {
      predefined_metric_type = "ASGAverageCPUUtilization"
    }

    target_value = 60.0
  }

  depends_on = ["aws_autoscaling_group.ecs_cluster_asg"]
}
