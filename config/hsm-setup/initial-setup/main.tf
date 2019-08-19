# variable region {
#   type = "string"
#   default = "${var.hsm_region}"
# }

terraform {
  backend "s3" {
    bucket = "rdso-challenge2"
    key    = "hsm.tfstate"
    region = "us-east-1"
  }
}

terraform {
  required_version = "0.12.5"
}

//variable region {}

provider "aws" {
  version = "~> 2.0"
  region  = "${local.region}"
}

//provider "tls" {}

locals {
  region = "${var.aws_region[terraform.workspace]}"
}


module "vpc" {
  source = "terraform-aws-modules/vpc/aws"

  name = "hsm-vpc"
  cidr = "10.6.0.0/16"

  azs = [
  "${local.region}c"]
  private_subnets = [
  "10.6.1.0/24"]
  public_subnets = [
  "10.6.101.0/24"]

  enable_nat_gateway = true
  single_nat_gateway = true

  tags = {
    Terraform   = "true"
    Environment = terraform.workspace
  }
}