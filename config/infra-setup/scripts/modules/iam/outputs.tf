output "ecs_lb_role_arn" {
  value = "${aws_iam_role.ecs_lb.arn}"
}

output "ecs_task_execution_role_arn" {
  value = "${aws_iam_role.ecs_execution_role.arn}"
}

output "iam_instance_profile" {
  value = "${aws_iam_instance_profile.ecs.arn}"
}

output "iam_instance_profile_name" {
  value = "${aws_iam_instance_profile.ecs.name}"
}
