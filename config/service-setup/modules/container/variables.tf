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
  default = "/health"
}

variable "matcher_ports" {
  default = "200,302"
}

variable "timeout" {
  default = 60
}

# variable "base_domain" {
#   default = "ugrouptech.com"
# }

variable "zone_id" {}

# variable "SPRING_DATA_MONGODB_HOST" {}

# variable "SPRING_DATA_MONGODB_USERNAME" {
#   default = "username"
# }
# variable "SPRING_DATA_MONGODB_PASSWORD" {
#   default = "password"
# }

# variable "SPRING_DATA_MONGODB_PORT" {
#   default = 27017
# }

variable "ds_redact_host" {}
variable "ds_images_host" {}
variable "ds_similarities_host" {}
variable "jwt_secret" {}
variable "business_user_password" {}
variable "business_supervisor_password" {}
variable "system_user_password" {}

variable "data_science_url" {}



variable "KAFKA_INTERNAL_IP" {
  default = "127.0.0.1"
}

variable "ENVIRONMENT_AUTHHOST_INTERNAL" {
  default = "127.0.0.1"
}

variable "postgres_username" { default = "postgres" }
variable "postgres_password" { default = "changeme" }
variable "postgres_url" { default = "foo" }

variable "region" {}

variable "cloud_watch_log_group_name" {}
variable "es_endpoint" {}

variable "logs_bucket" {}

variable "access_key" {}

variable "access_secret" {}
