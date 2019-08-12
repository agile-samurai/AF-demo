resource "aws_ecr_repository" "repo" {
  count = "${length(var.repo_names)}"

  name = "${element(var.repo_names, count.index)}"
}

provider "aws" {
  alias  = "east-2"
  region = "us-east-2"
}

provider "aws" {
  alias  = "west-1"
  region = "us-west-1"
}

provider "aws" {
  alias  = "west-2"
  region = "us-west-2"
}
