variable "ecs_cluster_name" {
  default = "temporary-cluster"
}

variable "private_subnets" {
  type = "list"
}

variable "public_subnets" {
  type = "list"
}

variable "vpc_id" {}

variable "environment" {
  description = "Environment for infrastructure"
}
