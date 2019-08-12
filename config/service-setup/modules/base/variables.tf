variable "availability_zones" {
  type        = "list"
  description = "Availability Zones to deploy ECS EC2 instances to"
  default     = ["us-east-1a", "us-east-1b"]
}

variable "project" {
  description = "Name of project"
  default     = "sbir-one"
}

variable "environment" {
  description = "Name of environment"
}

variable "peer_owner_id" {}

variable "peer_vpc_id" {}

variable "zone_id" {}
