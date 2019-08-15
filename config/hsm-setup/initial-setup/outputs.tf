locals {
  admin_password = aws_iam_user_login_profile.admin.encrypted_password
  cluster_id = aws_cloudhsm_v2_cluster.cloudhsm_v2_cluster.cluster_id
  cluster_state = aws_cloudhsm_v2_cluster.cloudhsm_v2_cluster.cluster_state
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

resource "local_file" "ec2_key" {
  sensitive_content = tls_private_key.hsm_key.private_key_pem
  filename = "${path.module}/key.pem"
}

resource "local_file" "ec2-ip" {
  content = aws_eip.this.public_ip
  filename = "${path.module}/ec2ip.txt"
}

resource "local_file" "hsm-ip" {
  content = aws_cloudhsm_v2_hsm.cloudhsm_v2_hsm.ip_address
  filename = "${path.module}/hsmip.txt"
}

resource "local_file" "user_pass" {
  sensitive_content = local.admin_password
  filename = "${path.module}/pass.txt"
}

output "hsm_cluster_id" {
  value = local.cluster_id
}

output "hsm_cluster_state" {
  value = local.cluster_state
}