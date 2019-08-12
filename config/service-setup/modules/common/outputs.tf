output "index_svc_alb_dns_name" {
  value = "${module.index_svc.index_alb_dns_name}"
}

output "reporting_alb_dns_name" {
  value = "${module.reporting.reporting_alb_dns_name}"
}
