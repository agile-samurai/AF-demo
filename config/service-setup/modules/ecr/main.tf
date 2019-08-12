resource "aws_ecr_repository" "repo" {
  count = "${length(var.repo_names)}"

  name = "${element(var.repo_names, count.index)}"
}
