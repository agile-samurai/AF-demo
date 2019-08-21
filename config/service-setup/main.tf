terraform {
  backend "s3" {
    bucket = "rdso-challenge2"
    key    = "rdso.tfstate"
    region = "us-east-1"
  }
}

terraform {
  required_version = "0.12.5"
}

provider "aws" {
  version = "~> 2.0"
  region  = "${local.region}"
}

locals {
  region = "${var.aws_region[terraform.workspace]}"
}


resource "aws_cloudwatch_log_group" "container" {
  name = "logs-${terraform.workspace}"

  tags = {
    Environment = terraform.workspace
  }
}

# data "terraform_remote_state" "hsm" {
#   backend   = "s3"
#   workspace = terraform.workspace
#   config = {
#     bucket = "${var.rs_bucket}"
#     key    = "hsm.tfstate"
#     region = "us-east-1"
#   }
# }

module "log-forwarding" {
  source           = "./modules/log-forwarding"
  es_endpoint      = module.elasticsearch.ElasticSearchEndpoint
  container_family = "containers"
  cwl_endpoint     = "logs.${local.region}.amazonaws.com"
}

resource "aws_lambda_permission" "cloudwatch_allow" {
  statement_id  = "cloudwatch_allow_challenge"
  action        = "lambda:InvokeFunction"
  function_name = module.log-forwarding.log_forward_lambda_arn
  principal     = "logs.${local.region}.amazonaws.com"
  source_arn    = aws_cloudwatch_log_group.container.arn
}

resource "aws_cloudwatch_log_subscription_filter" "cloudwatch_logs_to_es" {
  depends_on      = [aws_lambda_permission.cloudwatch_allow]
  name            = "cloudwatch_logs_to_elasticsearch-challenge"
  log_group_name  = aws_cloudwatch_log_group.container.name
  filter_pattern  = ""
  destination_arn = module.log-forwarding.log_forward_lambda_arn
}

data "aws_availability_zones" "available" {
}

resource "aws_route53_zone" "primary" {
  name = var.base_domain
}

data "aws_ami" "ecs_ami" {
  most_recent = true
  owners = [
    "amazon",
  ]

  filter {
    name = "owner-alias"
    values = [
      "amazon",
    ]
  }

  filter {
    name = "name"
    values = [
      "amzn-ami-*-amazon-ecs-optimized*",
    ]
  }
}

module "network" {
  source     = "./modules/networking"
  cidr_block = var.cidr_block[terraform.workspace]
}

module "ecs" {
  source      = "./modules/ecs-permissions"
  environment = terraform.workspace
  vpc_id      = module.network.vpc_id

  private_subnets = module.network.private_subnets
  public_subnets  = module.network.public_subnets
}

module "ecs-cluster" {
  source          = "./modules/ecs"
  vpc_id          = module.network.vpc_id
  admin_cidrs     = "0.0.0.0/0"
  zone_id         = aws_route53_zone.primary.zone_id
  private_subnets = module.network.private_subnets
  public_subnets  = module.network.public_subnets
}

module "elasticsearch" {
  source         = "./modules/elasticsearch"
  public-subnets = module.network.public_subnets
  vpc_id         = module.network.vpc_id
  region         = "${local.region}"
  #cidr_block     = var.cidr_block[terraform.workspace]
  instance_type = "m4.large.elasticsearch"
}

# module "mongodb" {
#   source           = "./modules/mongo"
#   vpc_id           = module.network.vpc_id
#   cluster_id       = module.ecs-cluster.ecs_cluster_id
#   ecs_cluster_name = module.ecs-cluster.ecs_cluster_name
#   private_subnets  = module.network.private_subnets
#   public_subnets   = module.network.public_subnets
#   cidr_block       = var.cidr_block[terraform.workspace]

#   MONGO_INITDB_ROOT_USERNAME = var.db_user
#   MONGO_INITDB_ROOT_PASSWORD = var.db_pass
# }

module "www" {
  source = "./modules/ui"

  execution_role_arn = module.ecs.ecs_task_execution_role_arn
  cluster_id         = module.ecs-cluster.ecs_cluster_id
  vpc_id             = module.network.vpc_id
  private_subnets    = module.network.private_subnets
  public_subnets     = module.network.public_subnets
  docker_image       = "${var.aws_account_id}.dkr.ecr.${var.ecr_image_region}.amazonaws.com/ui:${var.images_version}"
  container_family   = "www"

  instance_count             = 2
  timeout                    = 80
  container_port             = 80
  loadbalancer_port          = 80
  zone_id                    = aws_route53_zone.primary.zone_id
  server_url                 = module.server.dns_name
  cloud_watch_log_group_name = aws_cloudwatch_log_group.container.name
  region                     = "${local.region}"

  //cert_pem = tls_self_signed_cert.example.cert_pem  #cert data in pem format
  //private_key_pem = "${tls_private_key.example}"
}

module "server" {
  source = "./modules/container"

  execution_role_arn = module.ecs.ecs_task_execution_role_arn
  cluster_id         = module.ecs-cluster.ecs_cluster_id
  vpc_id             = module.network.vpc_id
  private_subnets    = module.network.private_subnets
  public_subnets     = module.network.public_subnets
  docker_image       = "${var.aws_account_id}.dkr.ecr.${var.ecr_image_region}.amazonaws.com/server:${var.images_version}"
  container_family   = "server"

  KAFKA_INTERNAL_IP = module.ecs-cluster.dns_name

  instance_count    = 2
  timeout           = 80
  container_port    = 8080
  loadbalancer_port = 80
  zone_id           = aws_route53_zone.primary.zone_id

  data_science_url           = module.datascience.dns_name
  es_endpoint                = module.elasticsearch.ElasticSearchEndpoint
  cloud_watch_log_group_name = aws_cloudwatch_log_group.container.name
  region                     = "${local.region}"
  logs_bucket                = "rdso-challenge2-logs"

  access_key    = var.access_key
  access_secret = var.access_secret
  aws_account_id = var.aws_account_id


  ds_redact_host="http://${module.ds-spaCy-model.dns_name}"   #spaCy url add http://
  ds_images_host="http://${module.datascience.dns_name}"
  ds_similarities_host="http://${module.datascience.dns_name}"
  jwt_secret=var.jwt_secret
  business_user_password=var.business_user_password
  business_supervisor_password=var.business_supervisor_password
  system_user_password=var.system_user_password
}

module "datascience" {
  source = "./modules/dsmodel"

  execution_role_arn = module.ecs.ecs_task_execution_role_arn
  cluster_id         = module.ecs-cluster.ecs_cluster_id
  vpc_id             = module.network.vpc_id
  private_subnets    = module.network.private_subnets
  public_subnets     = module.network.public_subnets
  docker_image       = "${var.aws_account_id}.dkr.ecr.${var.ecr_image_region}.amazonaws.com/data-science-service:${var.images_version}"
  container_family   = "data"
  #base_domain        = aws_route53_zone.primary.name

  health_check_path = "/metrics"
  instance_count    = 2
  timeout           = 20
  container_port    = 8000 #from container dockerfile
  loadbalancer_port = 80
  zone_id           = aws_route53_zone.primary.zone_id

  cloud_watch_log_group_name = aws_cloudwatch_log_group.container.name
  region                     = "${local.region}"
}


module "ds-spaCy-model" {
  source = "./modules/dsmodel-spaCy"

  execution_role_arn = module.ecs.ecs_task_execution_role_arn
  cluster_id         = module.ecs-cluster.ecs_cluster_id
  vpc_id             = module.network.vpc_id
  private_subnets    = module.network.private_subnets
  public_subnets     = module.network.public_subnets

  docker_image     = "jgontrum/spacyapi:en_v2"
  container_family = "spaCy"

  health_check_path = "/ui/"
  instance_count    = 1
  timeout           = 20
  container_port    = 80 #from container dockerfile
  loadbalancer_port = 80

  cloud_watch_log_group_name = aws_cloudwatch_log_group.container.name
  region                     = "${local.region}"
}
