variable "scala_repository_name" {
  description = "Reporting Scala ECR repo"
  default     = "reporting-repo"
}

variable "java_repository_name" {
  description = "Reporting Java ECR repo"
  default     = "reporting-java-service"
}

variable "haproxy_repository_name" {
  description = "Reporting HAProxy ECR repo"
  default     = "reporting-haproxy"
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

variable "access_key" {}
variable "secret_key" {}

variable "tag" {}
variable "mongo_pass" {}

variable "mongo_user" {
  type    = "string"
  default = "sbirroot"
}

variable "auth_host_internal_ip" {}

variable "kafka_internal_ip" {}
