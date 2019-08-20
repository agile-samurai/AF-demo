terraform {
  backend "s3" {
    bucket = "rdso-challenge2"
    key    = "hsm.tfstate"
    region    = "us-east-1"
  }
}

provider "aws" {
  version = "~> 2.0"
}

variable "aws_region" {
  type        = map(string)

  default = {
    #default = "us-east-1",
    dev  = "us-east-1",
    test = "us-east-2",
    prod = "us-west-1",
    concourse   = "us-west-2",
    infra   = "us-west-2"
  }
}

module "vpc" {
  source = "terraform-aws-modules/vpc/aws"

  name = "hsm-vpc"
  cidr = "10.6.0.0/16"

  azs = ["${var.aws_region["${terraform.workspace}"]}a"]
  private_subnets = ["10.6.1.0/24"]
  public_subnets = ["10.6.101.0/24"]

  enable_nat_gateway = true
  single_nat_gateway = true

  tags = {
    Terraform   = "true"
    Environment = terraform.workspace
  }
}
