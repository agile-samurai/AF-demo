locals {
  cluster_id    = aws_cloudhsm_v2_cluster.cloudhsm_v2_cluster.cluster_id
  cluster_state = aws_cloudhsm_v2_cluster.cloudhsm_v2_cluster.cluster_state
  hsm_id        = aws_cloudhsm_v2_hsm.cloudhsm_v2_hsm.hsm_id
}

output "hsm_cluster_id" {
  value = local.cluster_id
}

output "hsm_module_cluster_id" {
  value = local.hsm_id
}

output "hsm_cluster_state" {
  value = local.cluster_state
}

output "ec2_ip" {
  value = aws_eip.this.public_ip
}