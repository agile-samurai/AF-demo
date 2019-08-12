variable "region" {
  description = "Region to work in"
  default     = "us-east-1"
}

variable "environment" {
  description = "Environment for infrastructure"
  default     = "dev"
}

variable "reporting_count" {
  description = "Amount of reporting tasks to run"
  default     = 1
}

variable "index_svc_count" {
  description = "Amount of index service tasks to run"
  default     = 1
}

variable "access_key" {}
variable "secret_key" {}
variable "tag" {}
variable "db_pass" {}
variable "admin_pass" {}
variable "mongo_pass" {}
variable "reporting_key" {}
variable "zone_id" {}
