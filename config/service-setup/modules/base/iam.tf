resource "aws_iam_role_policy" "s3_read_write_delete_role_policy" {
  name   = "${var.environment}_s3_read_write_delete_role_policy"
  policy = "${file("${path.module}/policies/s3-read-write-delete-role-policy.json")}"
  role   = "${aws_iam_role.ecs_execution_role.id}"
}

resource "aws_iam_role_policy" "ses_send_email_role_policy" {
  name   = "${var.environment}_ses_send_email_role_policy"
  policy = "${file("${path.module}/policies/ses-send-email-role-policy.json")}"
  role   = "${aws_iam_role.ecs_execution_role.id}"
}
