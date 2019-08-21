resource "aws_cloudhsm_v2_cluster" "cloudhsm_v2_cluster" {
  hsm_type   = "hsm1.medium"
  subnet_ids = module.vpc.private_subnets

  tags = {
    Name = "test-aws_cloudhsm_v2_cluster"
  }

  depends_on = [module.vpc]
}

resource "aws_cloudhsm_v2_hsm" "cloudhsm_v2_hsm" {
  cluster_id        = aws_cloudhsm_v2_cluster.cloudhsm_v2_cluster.cluster_id
  availability_zone = data.aws_availability_zones.available.names[0]
  depends_on        = [aws_cloudhsm_v2_cluster.cloudhsm_v2_cluster]
}

data "aws_ami" "amazon_linux" {
  most_recent = true

  owners = ["amazon"]

  filter {
    name = "name"

    values = [
      "amzn-ami-hvm-*-x86_64-gp2",
    ]
  }

  filter {
    name = "owner-alias"

    values = [
      "amazon",
    ]
  }
}

resource "tls_private_key" "hsm_key" {
  algorithm = "RSA"
  rsa_bits  = 4096
}

resource "aws_key_pair" "hsm-key-pair" {
  public_key = tls_private_key.hsm_key.public_key_openssh
  depends_on = [tls_private_key.hsm_key]
}

resource "aws_security_group" "gateway-ingress" {
  name        = "allow_communication-${terraform.workspace}"
  description = "Allow Tomcat inbound traffic"
  vpc_id      = module.vpc.vpc_id

  ingress {
    from_port = 8080
    to_port   = 8080
    protocol  = "tcp"
    cidr_blocks = [
    "0.0.0.0/0"]
  }

  egress {
    from_port = 8080
    to_port   = 8080
    protocol  = "tcp"
    cidr_blocks = [
    "0.0.0.0/0"]
  }
  depends_on = [
  module.vpc]
}

resource aws_instance hsm_gateway {
  availability_zone           = data.aws_availability_zones.available.names[0]
  ami                         = data.aws_ami.amazon_linux.id
  monitoring                  = true
  instance_type               = "m4.large"
  subnet_id                   = tolist(module.vpc.public_subnets)[0]
  vpc_security_group_ids      = [module.vpc.default_security_group_id, aws_cloudhsm_v2_cluster.cloudhsm_v2_cluster.security_group_id, aws_security_group.gateway-ingress.id]
  associate_public_ip_address = true
  key_name                    = aws_key_pair.hsm-key-pair.key_name

  root_block_device {
    volume_type = "gp2"
    volume_size = 10
  }

  tags = {
    "Env" = "Public"
    "Name" = "HSM Client instance-${terraform.workspace}"
  }

  provisioner "file" {
    connection {
      type = "ssh"
      host = aws_instance.hsm_gateway.public_ip
      user = "ec2-user"
      private_key = file("${path.module}/key.pem")
    }

    source      = "templates/user_data.sh"
    destination = "/tmp/script.sh"
  }

  provisioner "file" {
    connection {
      type = "ssh"
      host = aws_instance.hsm_gateway.public_ip
      user = "ec2-user"
      private_key = file("${path.module}/key.pem")
    }


    source      = "customerCA.crt"
    destination = "/tmp/customerCA.crt"
  }

  provisioner "file" {
    connection {
      type = "ssh"
      host = aws_instance.hsm_gateway.public_ip
      user = "ec2-user"
      private_key = file("${path.module}/key.pem")
    }

    source      = "expect_script.sh"
    destination = "/tmp/expect_script.sh"
  }

  provisioner "file" {
    connection {
      type = "ssh"
      host = aws_instance.hsm_gateway.public_ip
      user = "ec2-user"
      private_key = file("${path.module}/key.pem")
    }

    source      = "setup_ec2.sh"
    destination = "/tmp/setup_ec2.sh"
  }

  provisioner "file" {
    connection {
      type = "ssh"
      host = aws_instance.hsm_gateway.public_ip
      user = "ec2-user"
      private_key = file("${path.module}/key.pem")
    }

    source      = "hsmgateway-0.0.1-SNAPSHOT.jar"
    destination = "/tmp/hsmgateway-0.0.1-SNAPSHOT.jar"
  }

  provisioner "remote-exec" {
    connection {
      type = "ssh"
      host = aws_instance.hsm_gateway.public_ip
      user = "ec2-user"
      private_key = file("${path.module}/key.pem")
    }

    inline = [
      "chmod +x /tmp/script.sh",
      "chmod +x /tmp/setup_ec2.sh",
      "chmod +x /tmp/expect_script.sh",
      "/tmp/script.sh",
      "/tmp/setup_ec2.sh ${aws_cloudhsm_v2_hsm.cloudhsm_v2_hsm.hsm_id} ${aws_iam_user_login_profile.admin.encrypted_password} ${aws_cloudhsm_v2_hsm.cloudhsm_v2_hsm.ip_address} ${aws_cloudhsm_v2_cluster.cloudhsm_v2_cluster.cluster_state}"
    ]
  }

  depends_on = [aws_cloudhsm_v2_hsm.cloudhsm_v2_hsm, aws_iam_user_login_profile.admin, aws_security_group.gateway-ingress, tls_private_key.hsm_key]
}

resource "aws_security_group_rule" "hsm_sg_rule" {
  from_port         = 22
  protocol          = "tcp"
  cidr_blocks       = ["${var.hsm_controller}/32", "10.0.1.0/24"]
  to_port           = 22
  type              = "ingress"
  security_group_id = module.vpc.default_security_group_id
  depends_on        = [module.vpc]
}

resource "local_file" "csr_file" {
  count             = aws_cloudhsm_v2_cluster.cloudhsm_v2_cluster.cluster_state == "UNINITIALIZED" ? 1 : 0
  sensitive_content = aws_cloudhsm_v2_cluster.cloudhsm_v2_cluster.cluster_certificates.0.cluster_csr
  filename          = "${path.module}/${aws_cloudhsm_v2_cluster.cloudhsm_v2_cluster.cluster_id}_ClusterCSR.csr"
  depends_on        = [aws_cloudhsm_v2_cluster.cloudhsm_v2_cluster]
}
resource "local_file" "aws_hardware_certificate" {
  count             = aws_cloudhsm_v2_cluster.cloudhsm_v2_cluster.cluster_state == "UNINITIALIZED" ? 1 : 0
  sensitive_content = aws_cloudhsm_v2_cluster.cloudhsm_v2_cluster.cluster_certificates.0.aws_hardware_certificate
  filename          = "${path.module}/${aws_cloudhsm_v2_cluster.cloudhsm_v2_cluster.cluster_id}_AwsHardwareCertificate.crt"
  depends_on        = [aws_cloudhsm_v2_cluster.cloudhsm_v2_cluster]
}
resource "local_file" "hsm_certificate" {
  count             = aws_cloudhsm_v2_cluster.cloudhsm_v2_cluster.cluster_state == "UNINITIALIZED" ? 1 : 0
  sensitive_content = aws_cloudhsm_v2_cluster.cloudhsm_v2_cluster.cluster_certificates.0.hsm_certificate
  filename          = "${path.module}/${aws_cloudhsm_v2_cluster.cloudhsm_v2_cluster.cluster_id}_HsmCertificate.crt"
  depends_on        = [aws_cloudhsm_v2_cluster.cloudhsm_v2_cluster]
}
resource "local_file" "manufacturer_hardware_certificate" {
  count             = aws_cloudhsm_v2_cluster.cloudhsm_v2_cluster.cluster_state == "UNINITIALIZED" ? 1 : 0
  sensitive_content = aws_cloudhsm_v2_cluster.cloudhsm_v2_cluster.cluster_certificates[0].manufacturer_hardware_certificate
  filename          = "${path.module}/${aws_cloudhsm_v2_cluster.cloudhsm_v2_cluster.cluster_id}_ManufacturerHardwareCertificate.crt"
  depends_on        = [aws_cloudhsm_v2_cluster.cloudhsm_v2_cluster]
}

resource "null_resource" "verify" {
  count             = aws_cloudhsm_v2_cluster.cloudhsm_v2_cluster.cluster_state == "UNINITIALIZED" ? 1 : 0
  provisioner "local-exec" {
    command = "./verify.sh ${aws_cloudhsm_v2_cluster.cloudhsm_v2_cluster.cluster_id}"
  }
  depends_on = [
    local_file.csr_file,
    local_file.aws_hardware_certificate,
    local_file.hsm_certificate,
  local_file.manufacturer_hardware_certificate, aws_cloudhsm_v2_cluster.cloudhsm_v2_cluster]
}

resource "null_resource" "sign_and_initialize" {
  count             = aws_cloudhsm_v2_cluster.cloudhsm_v2_cluster.cluster_state == "UNINITIALIZED" ? 1 : 0
  provisioner "local-exec" {
    command = "./sign_and_initialize.sh ${aws_cloudhsm_v2_cluster.cloudhsm_v2_cluster.cluster_id} ${aws_iam_user_login_profile.admin.encrypted_password} ${var.aws_region[terraform.workspace]}"
  }

  depends_on = [null_resource.verify, aws_cloudhsm_v2_cluster.cloudhsm_v2_cluster]
}

resource "local_file" "ec2_key" {
  sensitive_content = tls_private_key.hsm_key.private_key_pem
  filename          = "${path.module}/key.pem"
  depends_on        = [tls_private_key.hsm_key]
}

resource "null_resource" "key" {
  provisioner "local-exec" {
    command = "chmod 400 ${path.module}/key.pem"
  }

  depends_on = [local_file.ec2_key]
}