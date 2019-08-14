resource "aws_cloudhsm_v2_cluster" "cloudhsm_v2_cluster" {
  hsm_type   = "hsm1.medium"
  subnet_ids = module.vpc.private_subnets

  tags = {
    Name = "test-aws_cloudhsm_v2_cluster"
  }
}

resource "aws_cloudhsm_v2_hsm" "cloudhsm_v2_hsm" {
  cluster_id = aws_cloudhsm_v2_cluster.cloudhsm_v2_cluster.cluster_id
  availability_zone = "${var.region}c"
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

resource "aws_eip" "this" {
  vpc      = true
  instance = tolist(module.ec2.id)[0]
}

resource "tls_private_key" "hsm_key" {
  algorithm = "RSA"
  rsa_bits  = 4096
}

resource "aws_key_pair" "hsm-key-pair" {
  public_key = tls_private_key.hsm_key.public_key_openssh
}

module "ec2" {
  source         = "terraform-aws-modules/ec2-instance/aws"
  version        = "~> 2.0"
  instance_count = 1

  name                        = "example-normal"
  ami                         = data.aws_ami.amazon_linux.id
  instance_type               = "t2.medium"
  subnet_id                   = tolist(module.vpc.public_subnets)[0]
  vpc_security_group_ids      = [module.vpc.default_security_group_id, aws_cloudhsm_v2_cluster.cloudhsm_v2_cluster.security_group_id]
  associate_public_ip_address = true
  key_name                    = aws_key_pair.hsm-key-pair.key_name

  root_block_device = [
    {
      volume_type = "gp2"
      volume_size = 10
    },
  ]

  tags = {
    "Env" = "Public"
  }
}

resource "aws_security_group_rule" "hsm_sg_rule" {
  from_port         = 22
  protocol          = "tcp"
  cidr_blocks       = ["50.225.11.6/32"]
  to_port           = 22
  type              = "ingress"
  security_group_id = module.vpc.default_security_group_id
}