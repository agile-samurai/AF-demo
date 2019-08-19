locals {
  region = "${var.aws_region[terraform.workspace]}"
}


terraform {
  required_version = "0.12.5"
}

provider "aws" {
  version = "~> 2.0"
  region  = local.region
}

locals {
  hsm_ip = file("${path.module}/hsmip.txt")
  password = substr(file("${path.module}/pass.txt"), 0, 31)
}

data "aws_instance" "hsm-agent-instance" {
  filter {
    name = "tag:Name"
    values = ["HSM Client instance"]
  }
}

resource "null_resource" "provisioner" {
  connection {
    host = data.aws_instance.hsm-agent-instance.public_ip
    type = "ssh"
    user = "ec2-user"
    private_key = file("${path.module}/key.pem")
  }

  provisioner "file" {
    source = "customerCA.crt"
    destination = "/tmp/customerCA.crt"
  }

  provisioner "file" {
    source = "hsmip.txt"
    destination = "/tmp/hsmip.txt"
  }

  provisioner "file" {
    source = "hsm_id.txt"
    destination = "/tmp/hsm_id.txt"
  }

  provisioner "file" {
    source = "pass.txt"
    destination = "/tmp/pass.txt"
  }

  provisioner "file" {
    source = "expect_script.sh"
    destination = "/tmp/expect_script.sh"
  }

  provisioner "file" {
    source = "setup_ec2.sh"
    destination = "/tmp/setup_ec2.sh"
  }

  provisioner "remote-exec" {
    inline = [
      "chmod +x /tmp/setup_ec2.sh",
      "chmod +x /tmp/expect_script.sh",
      "bash -c '/tmp/setup_ec2.sh'",
    ]
  }
}