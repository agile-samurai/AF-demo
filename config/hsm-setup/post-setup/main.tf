variable region {
  type = "string"
  default = "us-west-1"
}
provider "aws" {
  version = "~> 2.0"
  region  = var.region
}

locals {
  cluster_id = file("${path.module}/cluster_id.txt")
}

data "aws_cloudhsm_v2_cluster" "hsm_cluster" {
  cluster_id = local.cluster_id
}

resource "local_file" "csr_file" {
  sensitive_content = data.aws_cloudhsm_v2_cluster.hsm_cluster.cluster_certificates.0.cluster_csr
  filename = "${path.module}/${local.cluster_id}_ClusterCSR.csr"
}
resource "local_file" "aws_hardware_certificate" {
  sensitive_content = data.aws_cloudhsm_v2_cluster.hsm_cluster.cluster_certificates.0.aws_hardware_certificate
  filename = "${path.module}/${local.cluster_id}_AwsHardwareCertificate.crt"
}
resource "local_file" "hsm_certificate" {
  sensitive_content = data.aws_cloudhsm_v2_cluster.hsm_cluster.cluster_certificates.0.hsm_certificate
  filename = "${path.module}/${local.cluster_id}_HsmCertificate.crt"
}
resource "local_file" "manufacturer_hardware_certificate" {
  sensitive_content = data.aws_cloudhsm_v2_cluster.hsm_cluster.cluster_certificates.0.manufacturer_hardware_certificate
  filename = "${path.module}/${local.cluster_id}_ManufacturerHardwareCertificate.crt"
}