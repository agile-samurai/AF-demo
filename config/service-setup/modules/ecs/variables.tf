variable "ecs_cluster_name" {
  default = "default-cluster"
}

variable "private_subnets" {
  type = "list"
}

variable "public_subnets" {
  type = "list"
}

variable "vpc_id" {}

variable "base_domain" {
  default = "ugrouptech.com"
}

variable "zone_id" {}
variable "admin_cidrs" {}
