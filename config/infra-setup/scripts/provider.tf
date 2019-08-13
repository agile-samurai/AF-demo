provider "aws" {
  region = "${var.aws_infra_region}"

  # version = "~> 1.22"
}

provider "aws" {
  region = "us-east-1"
  alias  = "provider_for_infra_bucket_region"

  # version = "~> 1.22"
}
