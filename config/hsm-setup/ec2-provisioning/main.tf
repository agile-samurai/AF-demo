variable region {
  type = "string"
  default = "us-west-1"
}
provider "aws" {
  version = "~> 2.0"
  region  = var.region
}

locals {
  hsm_ip = file("${path.module}/hsmip.txt")
  password = file("${path.module}/pass.txt")
  hsm_user_password = file("${path.module}/hsm_user_pass.txt")
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
    source = "hsmgateway-0.0.1-SNAPSHOT.jar"
    destination = "/tmp/hsmgateway.jar"
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
      "bash -c '/tmp/setup_ec2.sh ${substr(local.password, 0, 31)} ${local.hsm_user_password}'",
    ]
  }
}