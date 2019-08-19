variable "ecr_image_region" {
  default = "us-east-1"
}

# variable "ami" {
#   type        = map(string)
#   description = "AWS ECS AMI id"

#   default = {
#     # us-west-1      = "ami-bdafdbdd"
#     us-east-1 = "ami-cb2305a1"
#   }
#   # us-west-2 = "ami-644a431b"

#   # eu-west-1      = "ami-13f84d60"
#   # eu-central-1   = "ami-c3253caf"
#   # ap-northeast-1 = "ami-e9724c87"
#   # ap-southeast-1 = "ami-5f31fd3c"
#   # ap-southeast-2 = "ami-83af8ae0"
# }

# variable "ec2_ami" {
#   type        = map(string)
#   description = "AWS EC2 AMI id"

#   default = {
#     # us-west-1      = "ami-bdafdbdd"
#     us-east-1 = "ami-cb2305a1"
#   }
#   # us-west-2 = "ami-b70554c8"

#   # eu-west-1      = "ami-13f84d60"
#   # eu-central-1   = "ami-c3253caf"
#   # ap-northeast-1 = "ami-e9724c87"
#   # ap-southeast-1 = "ami-5f31fd3c"
#   # ap-southeast-2 = "ami-83af8ae0"
# }

variable "cidr_block" {
  type        = map(string)
  description = "Cidr block ranges per environment"

  default = {
    dev  = "10.1.0.0/16",
    test = "10.2.0.0/16",
    ft   = "10.3.0.0/16",
    prod = "10.4.0.0/16",
    infra = "10.5.0.0/16",
    hsm = "10.6.0.0/16"
  }
}

variable "aws_region" {
  type        = map(string)
  description = "region"

  default = {
    default = "us-eat-1",
    dev  = "us-east-1",
    test = "us-east-2",
    prod = "us-west-1",
    ft   = "us-west-2"
  }
}

variable "az_count" {
  default = "2"
}

variable "infra_version" {
  description = "Version of infrastructure deployed"
}

variable "images_version" {}

//variable "tag" {}

# variable "db_user" {
#   description = "Root username for mongo"
# }

# variable "db_pass" {
#   description = "Root password for mongo"
# }

variable "base_domain" {
  default = "ugrouptech.com"
}

variable "aws_account_id" {
}

variable "postgres_username" {
  description = "Root username for postgres"
  default     = "test"
}

variable "postgres_password" {
  description = "Root password for postgres"
  default     = "changeme"
}

variable "access_key" {}

variable "access_secret" {}




variable "jwt_secret" {}
variable "business_user_password" {}
variable "business_supervisor_password" {}
variable "system_user_password" {}
