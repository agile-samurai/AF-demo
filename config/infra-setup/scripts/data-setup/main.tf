variable "bucket_name_training" {}
variable "bucket_name_processed_data" {}
variable "bucket_name_state" {}
variable "bucket_name_ecr" {}

# resource "aws_ecr_repository" "repo" {
#   count = "${length(var.repo_names)}"
#
#   name = "${element(var.repo_names, count.index)}"
# }

provider "aws" {
  version = "~> 2.0"
  region  = "us-east-1"
}

resource "aws_route53_zone" "primary" {
  name = "theravenspoe.io"
}

resource "aws_s3_bucket" "training-data" {
  bucket = "${var.bucket_name_training}"
  acl    = "private"
  region = "us-east-1"
  force_destroy = true

  versioning {
    enabled = true
  }
}

resource "aws_s3_bucket" "ecr-data" {
  bucket = "${var.bucket_name_ecr}"
  acl    = "private"
  region = "us-east-1"
  force_destroy = true

  versioning {
    enabled = true
  }
}

resource "aws_s3_bucket" "processed-training-data" {
  bucket = "${var.bucket_name_processed_data}"
  acl    = "private"
  region = "us-east-1"
  force_destroy = true

  versioning {
    enabled = true
  }
}

resource "aws_s3_bucket" "state_file" {
  bucket = "${var.bucket_name_state}"
  acl    = "private"
  region = "us-east-1"
  force_destroy = true

  versioning {
    enabled = true
  }
}
