variable region {
  type    = "string"
  default = "us-west-1"
}

terraform {
  backend "s3" {
    bucket = "rdso-challenge2"
    key    = "hsm.tfstate"
    region    = "us-east-1"
  }
}

# terraform {
#   backend "local" {
#     path = "./terraform.tfstate"
#   }
# }

//variable hsm_region {}

provider "aws" {
  version = "~> 2.0"
  region  = "us-west-1"
  //  region  = "${var.hsm_region}"
}

module "vpc" {
  source = "terraform-aws-modules/vpc/aws"

  name = "hsm-vpc"
  cidr = "10.6.0.0/16"

  azs = [
  "us-west-1c"]
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
