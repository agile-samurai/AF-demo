variable "aws_region" {}

# variable "infra_ssh_key_name" {}

# variable "PATH_TO_PUBLIC_KEY" {}
# variable "PATH_TO_PRIVATE_KEY" {}

# variable "ami" {
#   type        = "map"
#   description = "AWS ECS AMI id"

#   default = {
#     us-east-1 = "ami-02da3a138888ced85"
#     us-east-2 = "ami-04328208f4f0cf1fe"
#   }
# }

variable "ec2_ami" {
  type        = "map"
  description = "AWS EC2 AMI id"

  default = {
    us-east-1 = "ami-02da3a138888ced85"
    us-east-2 = "ami-04328208f4f0cf1fe"
  }
}

variable "cidr_block" {
  default = "10.1.0.0/16"
}

variable "az_count" {
  default = "2"
}

# variable "ssckeypasswd" {}
# variable "keystorepasswd" {}

variable "sonarqube_rds_username" {}
variable "sonarqube_rds_password" {}
variable "rds_backup_retention_days" {
  default = 7
}

# variable "remote_state_bucket" {}
variable "environment" {}

variable "project_domain" {}
variable "project" {}
