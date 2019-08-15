terraform {
  backend "s3" {
    # config = {
    #   dynamodb_table = "dynamodb-terraform-state-lock"
    # }
  }
}

provider "aws" {
  version = "~> 2.0"
  region  = "${var.aws_region}"
}

module "ecr" {
  source = "./modules/ecr"

  repo_names = [
    "server",
    "data-science-service",
    "ui",
  ]
}
