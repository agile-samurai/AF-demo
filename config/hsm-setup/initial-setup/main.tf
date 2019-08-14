variable region {
  type = "string"
  default = "us-west-1"
}
provider "aws" {
  version = "~> 2.0"
  region  = var.region
}

module "vpc" {
  source = "terraform-aws-modules/vpc/aws"

  name = "hsm-vpc"
  cidr = "10.0.0.0/16"

  azs = [
  "us-west-1c"]
  private_subnets = [
  "10.0.1.0/24"]
  public_subnets = [
  "10.0.101.0/24"]

  enable_nat_gateway = true
  single_nat_gateway = true

  tags = {
    Terraform   = "true"
    Environment = "testing"
  }
}