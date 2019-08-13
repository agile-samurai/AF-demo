variable "execution_role_arn" {}
variable "cluster_id" {}
variable "vpc_id" {}

variable "private_subnets" {
  type = "list"
}

variable "public_subnets" {
  type = "list"
}

variable "docker_image" {}
variable "container_family" {}

variable "instance_count" {
  default = 1
}

variable "container_port" {
  default = 8080
}

variable "loadbalancer_port" {
  default = 80
}

variable "loadbalancer_port_https" {
  default = 443
}

variable "cpu" {
  default = 256
}

variable "memory" {
  default = 512
}

variable "health_check_path" {
  default = "/actuator/health"
}

variable "matcher_ports" {
  default = "200,302"
}

variable "timeout" {
  default = 60
}

variable "PERSISTENCE_MONGO_URL" {}

variable "base_domain" {
  default = "ugrouptech.com"
}

variable "zone_id" {}

variable "SPRING_DATA_MONGODB_HOST" {}

variable "SPRING_DATA_MONGODB_USERNAME" {
  default = "username"
}
variable "data_science_url" {}

variable "SPRING_DATA_MONGODB_PASSWORD" {
  default = "password"
}

variable "SPRING_DATA_MONGODB_PORT" {
  default = 27017
}

variable "KAFKA_INTERNAL_IP" {
  default = "127.0.0.1"
}

variable "ENVIRONMENT_AUTHHOST_INTERNAL" {
  default = "127.0.0.1"
}

variable "postgres_username" {}
variable "postgres_password" {}
variable "postgres_url" {default="foo"}

variable "region"{}

variable "cloud_watch_log_group_name" {}
variable "es_endpoint" {}