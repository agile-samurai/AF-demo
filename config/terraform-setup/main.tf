variable "bucket_name" {}
variable "aws_region" {}

provider "aws" {
  version = "~> 2.0"
  region  = "${var.aws_region}"
}

resource "aws_s3_bucket" "state-bucket" {
  bucket = "${var.bucket_name}"
  acl    = "private"

  versioning {
    enabled = true
  }
}
