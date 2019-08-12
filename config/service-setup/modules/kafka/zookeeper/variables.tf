variable "name" {
  default = "zookeeper"
}

variable "repository_name" {
  description = "Zookeeper ECR repo"
  default     = "zookeeper-repo"
}

variable "environment" {
  default = "dev"
}

variable "region" {
  default = "us-east-1"
}

variable "instance_count" {
  default = 1
}

variable "discovery_service_arn" {}

variable "private_dns" {
  description = "Private DNS namespace"
}

/*
variable "elb-name"{}
variable "dns-name"{}
*/
variable "service_desired_count" {
  default = 1
}

variable "ignore_changes" {
  default = "task_definition,container_definitions"
}

variable "ecs_cluster_id" {}

variable "ecs_cluster_name" {}

variable "ecs_service_role_arn" {}

variable "ecs_task_execution_role_arn" {}
