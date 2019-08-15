terraform {
  backend "s3" {
    bucket = "rdso-challenge2"
    key    = "rdso.tfstate"
    region = "us-east-1"
  }
}

resource "aws_cloudwatch_log_group" "container" {
  name = "logs-${terraform.workspace}"

  tags = {
    Environment = terraform.workspace
  }
}

module "log-forwarding" {
  source           = "./modules/log-forwarding"
  es_endpoint      = module.elasticsearch.ElasticSearchEndpoint
  container_family = "containers"
  cwl_endpoint     = "logs.${var.region}.amazonaws.com"
}

resource "aws_lambda_permission" "cloudwatch_allow" {
  statement_id  = "cloudwatch_allow_challenge"
  action        = "lambda:InvokeFunction"
  function_name = module.log-forwarding.log_forward_lambda_arn
  principal     = "logs.${var.region}.amazonaws.com"
  source_arn    = aws_cloudwatch_log_group.container.arn
}

resource "aws_cloudwatch_log_subscription_filter" "cloudwatch_logs_to_es" {
  depends_on      = [aws_lambda_permission.cloudwatch_allow]
  name            = "cloudwatch_logs_to_elasticsearch-challenge"
  log_group_name  = aws_cloudwatch_log_group.container.name
  filter_pattern  = ""
  destination_arn = module.log-forwarding.log_forward_lambda_arn
}

provider "aws" {
  version = "~> 2.0"
  region  = "us-east-1"
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
  region         = var.region
  vpc_cidr       = var.cidr_block
  instance_type  = "m4.large.elasticsearch"

}

module "mongodb" {
  source           = "./modules/mongo"
  vpc_id           = module.network.vpc_id
  cluster_id       = module.ecs-cluster.ecs_cluster_id
  ecs_cluster_name = module.ecs-cluster.ecs_cluster_name
  private_subnets  = module.network.private_subnets
  public_subnets   = module.network.public_subnets
  vpc_cidr         = var.cidr_block

  MONGO_INITDB_ROOT_USERNAME = var.db_user
  MONGO_INITDB_ROOT_PASSWORD = var.db_pass
}

module "www" {
  source = "./modules/ui"

  execution_role_arn = module.ecs.ecs_task_execution_role_arn
  cluster_id         = module.ecs-cluster.ecs_cluster_id
  vpc_id             = module.network.vpc_id
  private_subnets    = module.network.private_subnets
  public_subnets     = module.network.public_subnets
  docker_image       = "${var.aws_account_id}.dkr.ecr.us-east-1.amazonaws.com/ui:${var.images_version}"
  container_family   = "www"

  instance_count             = 1
  timeout                    = 80
  container_port             = 80
  loadbalancer_port          = 80
  zone_id                    = aws_route53_zone.primary.zone_id
  server_url                 = module.server.dns_name
  cloud_watch_log_group_name = aws_cloudwatch_log_group.container.name
  region                     = var.region
}

module "server" {
  source = "./modules/container"

  execution_role_arn = module.ecs.ecs_task_execution_role_arn
  cluster_id         = module.ecs-cluster.ecs_cluster_id
  vpc_id             = module.network.vpc_id
  private_subnets    = module.network.private_subnets
  public_subnets     = module.network.public_subnets
  docker_image       = "${var.aws_account_id}.dkr.ecr.us-east-1.amazonaws.com/server:${var.images_version}"
  container_family   = "server"
  base_domain        = aws_route53_zone.primary.name

  SPRING_DATA_MONGODB_HOST     = module.ecs-cluster.dns_name
  SPRING_DATA_MONGODB_USERNAME = var.db_user
  SPRING_DATA_MONGODB_PASSWORD = var.db_pass

  KAFKA_INTERNAL_IP        = module.ecs-cluster.dns_name
  PERSISTENCE_MONGO_URL    = module.ecs-cluster.dns_name
  SPRING_DATA_MONGODB_PORT = 27017
  instance_count           = 1
  timeout                  = 80
  container_port           = 8080
  loadbalancer_port        = 80
  zone_id                  = aws_route53_zone.primary.zone_id

  data_science_url           = module.datascience.dns_name
  es_endpoint                = module.elasticsearch.ElasticSearchEndpoint
  cloud_watch_log_group_name = aws_cloudwatch_log_group.container.name
  region                     = var.region
  logs_bucket                = "rdso-challenge2-logs"
}

module "datascience" {
  source = "./modules/dsmodel"

  execution_role_arn = module.ecs.ecs_task_execution_role_arn
  cluster_id         = module.ecs-cluster.ecs_cluster_id
  vpc_id             = module.network.vpc_id
  private_subnets    = module.network.private_subnets
  public_subnets     = module.network.public_subnets
  docker_image       = "${var.aws_account_id}.dkr.ecr.us-east-1.amazonaws.com/data-science-service:${var.images_version}"
  container_family   = "data"
  base_domain        = aws_route53_zone.primary.name

  health_check_path = "/metrics"
  instance_count    = 1
  timeout           = 20
  container_port    = 8080
  loadbalancer_port = 80
  zone_id           = aws_route53_zone.primary.zone_id

  cloud_watch_log_group_name = aws_cloudwatch_log_group.container.name
  region                     = var.region
}
