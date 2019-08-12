variable "repository_name" {
  description = "Index Service ECR repo"
  default     = "index-service-repo"
}

variable "ecs_cluster_id" {
  description = "SBIR ONE ECS Cluster Id"
}

variable "ecs_cluster_name" {
  description = "SBIR ONE ECS Cluster Name"
}

variable "vpc-id" {
  description = "SBIR ONE ECS VPC Id"
}

variable "desired_count" {
  default = "1"
}

variable "environment" {}

variable "region" {}

variable "route53_zone_id" {}

variable "ecs_task_execution_role_arn" {}

variable "ecs_service_role_arn" {}
variable "ecs_scaling_role_arn" {}

variable "security_groups" {
  type = "list"
}

variable "tag" {}
variable "db_pass" {}
variable "admin_pass" {}
variable "mongo_pass" {}

variable "kafka_internal_ip" {}
