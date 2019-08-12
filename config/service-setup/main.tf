# terraform {
#   backend "s3" {
#     bucket = "mdas-challenge-ugroup-new"
#     key    = "mdas-terraform.tfstate"
#     region = "us-east-1"
#   }
# }

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
  region  = "us-west-2"
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

module "kafka" {
  source                      = "./modules/kafka"
  vpc_id                      = module.network.vpc_id
  region                      = var.region
  environment                 = terraform.workspace
  private_subnets             = module.network.private_subnets
  public_subnets              = module.network.public_subnets
  zookeeper_svc_count         = 1
  broker_svc_count            = 1
  ecs_cluster_name            = module.ecs-cluster.ecs_cluster_name
  ecs_cluster_id              = module.ecs-cluster.ecs_cluster_id
  sg-allow-ssh                = module.ecs-cluster.sg-allow-ssh
  sg-allow-cluster            = module.ecs-cluster.sg-allow-cluster
  ecs_task_execution_role_arn = module.ecs.ecs_task_execution_role_arn
  ecs_autoscale_role_arn      = module.ecs.ecs_autoscale_role_arn
  ecs_service_role_arn        = module.ecs.ecs_service_role_arn
  ecs_instance_role_name      = module.ecs.ecs_instance_role_name
  ecs_instance_ip             = module.ecs-cluster.dns_name
  zone_id                     = aws_route53_zone.primary.zone_id
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

module "postgres" {
  source = "./modules/postgres"

  username     = var.db_user
  rds_password = var.db_pass
  db_name      = "postgres"

  private_subnets = module.network.private_subnets
  public_subnets  = module.network.public_subnets
  vpc_id          = module.network.vpc_id
}

module "www" {
  source = "./modules/ui"

  execution_role_arn = module.ecs.ecs_task_execution_role_arn
  cluster_id         = module.ecs-cluster.ecs_cluster_id
  vpc_id             = module.network.vpc_id
  private_subnets    = module.network.private_subnets
  public_subnets     = module.network.public_subnets
  docker_image       = "${var.aws_account_id}.dkr.ecr.us-east-1.amazonaws.com/ui:${var.image-version}"
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

module "nginx" {
  source = "./modules/ui"

  execution_role_arn = module.ecs.ecs_task_execution_role_arn
  cluster_id         = module.ecs-cluster.ecs_cluster_id
  vpc_id             = module.network.vpc_id
  private_subnets    = module.network.private_subnets
  public_subnets     = module.network.public_subnets
  docker_image       = "nginxdemos/hello:latest"
  container_family   = "nginx"

  instance_count    = 1
  timeout           = 80
  container_port    = 80
  loadbalancer_port = 80
  zone_id           = aws_route53_zone.primary.zone_id

  server_url                 = "foo"
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
  docker_image       = "${var.aws_account_id}.dkr.ecr.us-east-1.amazonaws.com/server:${var.image-version}"
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
  container_port           = 80
  loadbalancer_port        = 80
  zone_id                  = aws_route53_zone.primary.zone_id

  postgres_username          = var.db_user
  postgres_password          = var.db_pass
  postgres_url               = module.postgres.postgres_url
  data_science_url           = module.datascience.dns_name
  es_endpoint                = module.elasticsearch.ElasticSearchEndpoint
  cloud_watch_log_group_name = aws_cloudwatch_log_group.container.name
  region                     = var.region
}

module "datascience" {
  source = "./modules/dsmodel"

  execution_role_arn = module.ecs.ecs_task_execution_role_arn
  cluster_id         = module.ecs-cluster.ecs_cluster_id
  vpc_id             = module.network.vpc_id
  private_subnets    = module.network.private_subnets
  public_subnets     = module.network.public_subnets
  docker_image       = "${var.aws_account_id}.dkr.ecr.us-east-1.amazonaws.com/data-science-service:${var.image-version}"
  container_family   = "data"
  base_domain        = aws_route53_zone.primary.name

  health_check_path = "/health/check"
  instance_count    = 1
  timeout           = 20
  container_port    = 8080
  loadbalancer_port = 80
  zone_id           = aws_route53_zone.primary.zone_id

  cloud_watch_log_group_name = aws_cloudwatch_log_group.container.name
  region                     = var.region
}
