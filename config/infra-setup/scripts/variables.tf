
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

# variable "ec2_ami" {
#   type        = "map"
#   description = "AWS EC2 AMI id"

#   default = {
#     us-east-1 = "ami-02da3a138888ced85"
#     us-east-2 = "ami-04328208f4f0cf1fe"
#   }
# }

variable "cidr_block" {
  type        = map(string)
  description = "Cidr block ranges per environment"

  default = {
    #default  = "10.1.0.0/16",
    dev  = "10.1.0.0/16",
    test = "10.2.0.0/16",
    ft   = "10.3.0.0/16",
    prod = "10.4.0.0/16",
    infra = "10.5.0.0/16",
    hsm = "10.6.0.0/16",
    concourse = "10.20.0.0/16",
    infra = "10.22.0.0/16"
  }
}

variable "aws_region" {
  type        = map(string)
  description = "region. infra includes hsm,sonarqube,concourse"

  default = {
    #default = "us-east-1",
    dev  = "us-east-1",
    test = "us-east-2",
    prod = "us-west-1",
    concourse   = "us-west-2",
    infra   = "us-west-2"
  }
}

variable "az_count" {
  default = "2"
}

# variable "ssckeypasswd" {}
# variable "keystorepasswd" {}

variable "sonarqube_rds_username" {
  default = "postgres"
}
variable "sonarqube_rds_password" {
  default = "longerthan8chars"
}
variable "rds_backup_retention_days" {
  default = 7
}

# variable "remote_state_bucket" {}

#variable "project_domain" {}
variable "project" {}
