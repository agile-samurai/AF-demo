output "reporting_service_name" {
  value = "${aws_ecs_service.reporting-service.name}"
}

output "reporting_alb_dns_name" {
  value = "${data.aws_alb.sbirone-alb.dns_name}"
}

output "target_group_arn" {
  value = "${aws_alb_target_group.reporting-alb-target.arn}"
}
