output "infra_vpc_id" {
  value = "${module.network.vpc_id}"
}

output "infra_public_subnets" {
  value = "${module.network.public_subnets}"
}

output "infra_private_subnets" {
  value = "${module.network.private_subnets}"
}

output "KibanaEndpoint" {
  value = "${module.elasticsearch.KibanaEndpoint}"
}

output "ElasticSearchEndpoint" {
  value = "${module.elasticsearch.ElasticSearchEndpoint}"
}

output "sso-url" {
  value = "${module.sso.dns_name}"
}

output "topic-development-url" {
  value = "${module.topicdev.dns_name}"
}

output "link-url" {
  value = "${module.link.dns_name}"
}

output "ulisses-url" {
  value = "${module.ulisses.dns_name}"
}

output "sbir-one-dashboard-url" {
  value = "${module.sbir-one-dashboard.dns_name}"
}

output "surs-core-url" {
  value = "${module.surs-container.surs-core-url}"
}

output "surs-reporting-url" {
  value = "${module.surs-container.surs-reporting-url}"
}
