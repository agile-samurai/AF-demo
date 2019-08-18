terraform {
  backend "s3" {
    # config = {
    #   dynamodb_table = "dynamodb-terraform-state-lock"
    # }
  }
}

terraform {
  required_version = "0.12.5"
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
