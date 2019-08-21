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

output "server_dns_name" {
  value = module.server.server_dns_name
}

#hsm




# output "hsm_cluster_id"{
#   value = data.terraform_remote_state.hsm.outputs.hsm_cluster_id
# }
# output "hsm_cluster_state"{
#   value = data.terraform_remote_state.hsm.outputs.hsm_cluster_state
# }

# output "hsm_module_eni_id"{
#   value = data.terraform_remote_state.hsm.outputs.hsm_module_eni_id
# }


# output "hsm_module_cluster_id"{
#   value = data.terraform_remote_state.hsm.outputs.hsm_module_cluster_id
# }

# output "hsm_ec2_ip"{
#   value = data.terraform_remote_state.hsm.outputs.ec2_ip
# }
