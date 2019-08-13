output "dns_name" {
  value = "${aws_alb.lb.dns_name}"
}

output "server_dns_name" {
  value = "${aws_alb.lb.dns_name}"
}
