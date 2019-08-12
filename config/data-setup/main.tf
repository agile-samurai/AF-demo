terraform {
  backend "s3" {
    bucket         = "mdas-tf-backend-3"
    key            = "mdas-terraform-data.tfstate"
    region         = "us-east-1"
    dynamodb_table = "dynamodb-terraform-state-lock"
  }
}

provider "aws" {
  version = "~> 2.0"
  region  = "us-east-1"
}

resource "aws_s3_bucket" "training-data" {
  bucket = "mdas-ugroup-training-data"
  acl    = "private"
  region = "us-east-1"

  versioning {
    enabled = true
  }
}

resource "aws_s3_bucket" "processed-training-data" {
  bucket = "mdas-ugroup-processed-training-data"
  acl    = "private"
  region = "us-east-1"

  versioning {
    enabled = true
  }
}

resource "aws_iam_service_linked_role" "elasticseasrch" {
  aws_service_name = "es.amazonaws.com"
}
