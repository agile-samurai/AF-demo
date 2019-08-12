variable "region" {
  description = "Region to work in"
  default     = "us-east-1"
}

variable "environment" {
  description = "Environment for infrastructure"
  default     = "dev"
}

variable "private_dns" {
  description = "Private DNS namespace"
  default     = "sbirone.local"
}

variable "zookeeper_svc_count" {
  description = "Amount of zookeeper service tasks to run"
  default     = 1
}

variable "broker_svc_count" {
  description = "Amount of kafka broker service tasks to run"
  default     = 1
}

variable "vpc_id" {}
variable "ecs_cluster_name" {}
variable "ecs_cluster_id" {}
variable "sg-allow-cluster" {}
variable "sg-allow-ssh" {}

variable "ecs_task_execution_role_arn" {}
variable "ecs_autoscale_role_arn" {}
variable "ecs_service_role_arn" {}
variable "ecs_instance_role_name" {}

variable "ecs_instance_ip" {}

variable "private_subnets" {
  type        = "list"
  description = "private subnets used in vpc"
}

variable "public_subnets" {
  type        = "list"
  description = "public subnets used in vpc"
}

variable "zone_id" {}
