variable "region" {
  default = "us-west-2"
}

variable "ami" {
  type        = map(string)
  description = "AWS ECS AMI id"

  default = {
    # us-west-1      = "ami-bdafdbdd"
    us-west-2 = "ami-ec75908c"
  }
  # us-west-2 = "ami-644a431b"

  # eu-west-1      = "ami-13f84d60"
  # eu-central-1   = "ami-c3253caf"
  # ap-northeast-1 = "ami-e9724c87"
  # ap-southeast-1 = "ami-5f31fd3c"
  # ap-southeast-2 = "ami-83af8ae0"
}

variable "ec2_ami" {
  type        = map(string)
  description = "AWS EC2 AMI id"

  default = {
    # us-west-1      = "ami-bdafdbdd"
    us-west-2 = "ami-ec75908c"
  }
  # us-west-2 = "ami-b70554c8"

  # eu-west-1      = "ami-13f84d60"
  # eu-central-1   = "ami-c3253caf"
  # ap-northeast-1 = "ami-e9724c87"
  # ap-southeast-1 = "ami-5f31fd3c"
  # ap-southeast-2 = "ami-83af8ae0"
}

variable "cidr_block" {
  type        = map(string)
  description = "Cidr block ranges per environment"

  default = {
    default = "10.1.0.0/16",
    dev     = "10.1.0.0/16",
    test    = "10.1.0.0/16",
    ft      = "10.1.0.0/16",
    prod    = "10.1.0.0/16"
  }
}

variable "az_count" {
  default = "2"
}

variable "infra_version" {
  description = "Version of infrastructure deployed"
}

variable "image-version" {
  default = "latest"
}

//variable "tag" {}

variable "db_user" {
  description = "Root username for mongo"
}

variable "db_pass" {
  description = "Root password for mongo"
}

variable "base_domain" {
  default = "ugrouptech.com"
}

variable "aws_account_id" {

}