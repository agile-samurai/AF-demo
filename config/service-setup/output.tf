output "infra_vpc_id" {
  value = module.network.vpc_id
}

output "compute_dns" {
  value = module.ecs-cluster.dns_name
}

output "infra_public_subnets" {
  value = module.network.public_subnets
}

output "infra_private_subnets" {
  value = module.network.private_subnets
}

output "KibanaEndpoint" {
  value = module.elasticsearch.KibanaEndpoint
}

output "ElasticSearchEndpoint" {
  value = module.elasticsearch.ElasticSearchEndpoint
}

output "www-url" {
  value = module.www.dns_name
}

output "data-url" {
  value = module.datascience.dns_name
}
