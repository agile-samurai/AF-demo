variable "execution_role_arn" {}
variable "cluster_id" {}
variable "vpc_id" {}

variable "region" {}

variable "account_id" {
  default = "account"
}

variable "private_subnets" {
  type = "list"
}

variable "public_subnets" {
  type = "list"
}

variable "docker_image" {}

variable "container_family" {}

variable "instance_count" {}

variable "container_port" {}

variable "loadbalancer_port" {}


#variable "certificate_arn" {}

variable "cpu" {
  default = 1024
}

variable "memory" {
  default = 2048
}

variable "health_check_path" {}


variable "interval" {
  default = 30
}

variable "timeout" {
  default = 14
}

variable "healthy_threshold" {
  default = 4
}

variable "unhealthy_threshold" {
  default = 6
}

variable "spring_datasource_userame" {
  default = ""
}

variable "spring_datasource_password" {
  default = ""
}

variable "spring_datasource_url" {
  default = ""
}

variable "spring_datasource_driver_class_name" {
  default = "org.postgresql.Driver"
}

variable "spring_jpa_database" {
  default = "postgresql"
}

variable "spring_jpa_database_platform" {
  default = "org.hibernate.dialect.PostgreSQLDialect"
}

# variable "base_domain" {}

variable "data_science_host" {
  default = ""
}

variable "platform_version" {
  default = ""
}

# variable "zone_id" {}

variable "access_key" {
  default = "fake"
}

variable "access_secret" {
  default = "fake"
}

variable "postgres_host" {
  default = "mdas-rds"
}

variable "elastic_search_host" { default = "" }
variable "elastic_search_port" { default = "80" }
variable "cloud_watch_log_group_name" {}

variable "postgres_port" {
  default = 5432
}

variable "matcher_ports" {
  default = "200,302"
}