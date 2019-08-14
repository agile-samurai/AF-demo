locals {
  admin_password = aws_iam_user_login_profile.admin.encrypted_password
  cluster_id = aws_cloudhsm_v2_cluster.cloudhsm_v2_cluster.cluster_id
  cluster_state = aws_cloudhsm_v2_cluster.cloudhsm_v2_cluster.cluster_state
}

output "admin-user-password" {
  value = local.admin_password
}

resource "local_file" "cluster_id_file" {
  count = 1
  content = local.cluster_id
  filename = "${path.module}/cluster_id.txt"
}

resource "local_file" "cluster_state_file" {
  content = local.cluster_state
  filename = "${path.module}/cluster_state.txt"
}

output "hsm_cluster_id" {
  value = local.cluster_id
}

output "hsm_cluster_state" {
  value = local.cluster_state
}