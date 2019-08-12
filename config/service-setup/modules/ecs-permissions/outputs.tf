output "ecs_task_execution_role_arn" {
  value = "${aws_iam_role.ecs_execution_role.arn}"
}

output "ecs_instance_role_name" {
  value = "${aws_iam_role.ecs_instance_role.name}"
}

output "ecs_autoscale_role_arn" {
  value = "${aws_iam_role.ecs_autoscale_role.arn}"
}

output "ecs_service_role_arn" {
  value = "${aws_iam_role.ecs_service_role.arn}"
}

output "iam_instance_profile" {
  value = "${aws_iam_instance_profile.ecs.arn}"
}

output "iam_instance_profile_name" {
  value = "${aws_iam_instance_profile.ecs.name}"
}
