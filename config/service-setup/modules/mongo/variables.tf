variable "cluster_id" {}
variable "ecs_cluster_name" {}

variable "private_subnets" {
  type = "list"
}

variable "public_subnets" {
  type = "list"
}

variable "MONGO_INITDB_ROOT_USERNAME" {
  default = "fooset"
}

variable "MONGO_INITDB_ROOT_PASSWORD" {
  default = "password"
}

variable "vpc_id" {}
variable "vpc_cidr" {}
