terraform {
  backend "s3" {
    #bucket = "rdso-challenge2"
    #key    = "infra.tfstate"
    #region = "us-east-1"
  }
}

terraform {
  required_version = "0.12.5"
}

# Fetch AZs in the current region
data "aws_availability_zones" "available" {}

data "aws_ami" "ecs_ami" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "owner-alias"
    values = ["amazon"]
  }

  filter {
    name   = "name"
    values = ["amzn-ami-*-amazon-ecs-optimized*"]
  }
}

module "network" {
  source      = "./modules/network"
  environment = "${terraform.workspace}"
  cidr_block  = "${var.cidr_block}"
}

module "iam" {
  source      = "./modules/iam"
  environment = "${terraform.workspace}"
}

module "ecs" {
  source           = "./modules/ecs"
  ecs_cluster_name = "${var.project}-infrastructure-services"
}

module "sonarqube" {
  source                      = "./modules/sonarqube"
  iam_instance_profile        = "${module.iam.iam_instance_profile}"
  vpc_id                      = "${module.network.vpc_id}"
  vpc-cidr                    = "${var.cidr_block}"
  region                      = "${var.aws_infra_region}"
  ecs_cluster_name            = "${module.ecs.ecs_cluster_name}"
  cluster_id                  = "${module.ecs.ecs_cluster_id}"
  sg-allow-ssh                = "${module.network.sg-allow-ssh}"
  sg-allow-cluster            = "${module.network.sg-allow-cluster}"
  sg-allow-inbound            = "${module.network.sg-allow-inbound}"
  service_desired_count       = 1
  environment                 = "${terraform.workspace}"
  sonarqube_rds_username      = "${var.sonarqube_rds_username}"
  sonarqube_rds_password      = "${var.sonarqube_rds_password}"
  rds_backup_retention_days   = 7
  sonarqube_db_port           = 3306
  ami                         = "${data.aws_ami.ecs_ami.id}"
  private-subnets             = "${module.network.private_subnets}"
  public-subnets              = "${module.network.public_subnets}"
  ecs-cluster-name            = "${terraform.workspace}-sonarqube"
  sg_description              = "allow Sonarqube service"
  sg_from_port                = 9000
  sg_to_port                  = 9000
  sg_protocol                 = "tcp"
  sg_cidr_block               = "0.0.0.0/0"
  lb_target_group_port        = 9000
  host_port                   = 9000
  lb_target_group_protocol    = "HTTP"
  lb_health_check_path        = "/"
  lb_listener_port            = 80
  lb_listener_protocol        = "HTTP"
  cpu                         = 1024
  memory                      = 2048
  asg_instance_type           = "t2.medium"
  load_balancer_type          = "application"
  ecs_lb_role_arn             = "${module.iam.ecs_lb_role_arn}"
  ecs_task_execution_role_arn = "${module.iam.ecs_task_execution_role_arn}"
  project                     = "${var.project}"
}
