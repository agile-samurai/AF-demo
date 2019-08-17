variable "ecs-cluster-name" {}

variable "container_port" {
  default = 9000
}

variable "container_name" {
  default = "sonarqube"
}

variable "ecs_lb_role_arn" {}

variable "service_name" {
  default = "sonarqube"
}

variable "container_name_https" {
  default = "sonarqube_https"
}

variable "loadbalancer_port" {
  default = 80
}

variable "vpc_id" {}

variable "vpc-cidr" {}

variable "sonarqube_rds_sg" {
  default     = "rds_sg"
  description = "Tag Name for RDS sg"
}

variable "sonarqube_rds_storage" {
  default     = "250"
  description = "Storage size in GB"
}

variable "sonarqube_rds_storage_type" {
  default     = "gp2"
  description = "Storage type"
}

variable "sonarqube_rds_engine" {
  default     = "mysql"
  description = "Engine type, example values mysql, postgres"
}

variable "sonarqube_rds_engine_version" {
  description = "Engine version"

  default = {
    mysql    = "5.7.21"
    postgres = "9.6.8"
  }
}

variable "sonarqube_db_instance_class" {
  default     = "db.t2.micro"
  description = "Instance class"
}

variable "sonarqube_rds_dbname" {
  default     = "sonarqube"
  description = "db name"
}

variable "sonarqube_rds_username" {}
variable "sonarqube_rds_password" {}
variable "ecs_task_execution_role_arn" {}

variable "environment" {}

variable "private-subnets" {
  type = "list"
}

variable "rds_backup_retention_days" {}
variable "load_balancer_type" {}

variable "public-subnets" {
  type = "list"
}

variable "region" {}

variable "ami" {}

variable "cpu" {}

variable "memory" {}

variable "sonarqube_image" {
  #default = "madhujoshi/my-sonar-7"
  default = "owasp/sonarqube"
}

variable "sg-allow-inbound" {}
variable "sg-allow-cluster" {}
variable "sg-allow-ssh" {}
variable "sg_description" {}
variable "sg_protocol" {}
variable "sg_to_port" {}
variable "sg_from_port" {}
variable "sg_cidr_block" {}
variable "iam_instance_profile" {}
variable "asg_instance_type" {}
variable "lb_health_check_path" {}
variable "lb_target_group_protocol" {}
variable "lb_target_group_port" {}
variable "lb_listener_port" {}
variable "host_port" {}
variable "lb_listener_protocol" {}
variable "sonarqube_db_port" {}
variable "service_desired_count" {}

variable "ecs_cluster_name" {}
variable "cluster_id" {}
variable "project" {}
