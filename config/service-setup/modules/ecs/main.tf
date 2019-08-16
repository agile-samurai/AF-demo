resource "aws_ecs_cluster" "infrastructure" {
  name = "${terraform.workspace}-${var.ecs_cluster_name}"
}

resource "aws_iam_role" "ecs-service-role" {
  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "ecs.amazonaws.com"
      },
      "Effect": "Allow",
      "Sid": ""
    }
  ]
}
EOF
}

resource "aws_iam_role_policy_attachment" "ecs-service-role-attachment" {
  role = "${aws_iam_role.ecs-service-role.name}"
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonEC2ContainerServiceRole"
}

data "aws_ssm_parameter" "ecs-ami" {
  name = "/aws/service/ecs/optimized-ami/amazon-linux/recommended"
}

locals {
  ecs_optimized_ami = "${replace(element(split(":", element(split(",", data.aws_ssm_parameter.ecs-ami.value), 2)), 1), "\"", "")}"
}

resource "aws_instance" "compute" {
  # ECS-optimized AMI for us-east-1 updated to Jun2019
  ami = "${local.ecs_optimized_ami}"
  instance_type = "r3.xlarge"

  tags = {
    Name = "ECS Compute"
    Environment = "${terraform.workspace}"
  }

  user_data = "${data.template_file.userdata.rendered}"

  iam_instance_profile = "${aws_iam_instance_profile.compute.name}"
  vpc_security_group_ids = ["${aws_security_group.compute.id}"]
  subnet_id = "${var.public_subnets[0]}"

  associate_public_ip_address = true
}

data "template_file" "userdata" {
  template = "${file("${path.module}/userdata/ecs-instances.sh")}"

  vars = {
    cluster-name = "${aws_ecs_cluster.infrastructure.name}"
  }
}

# resource "aws_efs_file_system" "mongo-efs" {
#   creation_token = "mongo-efs"

#   tags = {
#     Name = "${aws_ecs_cluster.infrastructure.name}.efs"
#     Environment = "${terraform.workspace}"
#   }
# }

//MongoDB Security Group
resource "aws_security_group" "compute" {
  name = "compute"
  vpc_id = "${var.vpc_id}"
  description = "Allow all inbound traffic from VPC and SSH from world"

  tags = {
    Name = "Infra Compute"
    Environment = "${terraform.workspace}"
  }

  lifecycle {
    create_before_destroy = true
  }

  ingress {
    protocol = "tcp"
    from_port = 7474
    to_port = 7474
    cidr_blocks = ["0.0.0.0/0"]

    # cidr_blocks = ["${var.vpc-cidr}"]
  }

  ingress {
    protocol = "tcp"
    from_port = 7687
    to_port = 7687
    cidr_blocks = ["0.0.0.0/0"]

    # cidr_blocks = ["${var.vpc-cidr}"]
  }

  ingress {
    protocol = "tcp"
    from_port = 5672
    to_port = 5672
    cidr_blocks = ["0.0.0.0/0"]

    # cidr_blocks = ["${var.vpc-cidr}"]
  }

  ingress {
    protocol = "tcp"
    from_port = 15672
    to_port = 15672
    cidr_blocks = ["0.0.0.0/0"]

    # cidr_blocks = ["${var.vpc-cidr}"]
  }

  ingress {
    protocol = "tcp"
    from_port = 27017
    to_port = 27017
    cidr_blocks = ["0.0.0.0/0"]

    # cidr_blocks = ["${var.vpc-cidr}"]
  }

  ingress {
    from_port = 2181
    to_port = 2181
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port = 2181
    to_port = 2181
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port = 9092
    to_port = 9092
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port = 9092
    to_port = 9092
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port = 8081
    to_port = 8081
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port = 8081
    to_port = 8081
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    protocol = -1
    from_port = 0
    to_port = 0
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    protocol = "tcp"
    from_port = 7474
    to_port = 7474
    cidr_blocks = ["0.0.0.0/0"]

    # cidr_blocks = ["${var.vpc-cidr}"]
  }

  ingress {
    protocol = "tcp"
    from_port = 7687
    to_port = 7687
    cidr_blocks = ["0.0.0.0/0"]

    # cidr_blocks = ["${var.vpc-cidr}"]
  }

  ingress {
    protocol = "tcp"
    from_port = 5672
    to_port = 5672
    cidr_blocks = ["0.0.0.0/0"]

    # cidr_blocks = ["${var.vpc-cidr}"]
  }

  ingress {
    protocol = "tcp"
    from_port = 15672
    to_port = 15672
    cidr_blocks = ["0.0.0.0/0"]

    # cidr_blocks = ["${var.vpc-cidr}"]
  }

  ingress {
    protocol = "tcp"
    from_port = 27017
    to_port = 27017
    cidr_blocks = ["0.0.0.0/0"]

    # cidr_blocks = ["${var.vpc-cidr}"]
  }

  egress {
    protocol = -1
    from_port = 0
    to_port = 0
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# ami-5253c32d

resource "aws_iam_role" "compute" {
  # name = "mongo"
  assume_role_policy = <<EOF
{
"Version": "2012-10-17",
"Statement": [
  {
    "Effect": "Allow",
    "Principal": {
      "Service": "ec2.amazonaws.com"
    },
    "Action": "sts:AssumeRole"
  }
]
}
EOF
}

resource "aws_iam_role_policy" "compute" {
  # name = "ecs_instance_role"
  role = "${aws_iam_role.compute.id}"

  policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "ecs:CreateCluster",
        "ecs:DeregisterContainerInstance",
        "ecs:DiscoverPollEndpoint",
        "ecs:Poll",
        "ecs:RegisterContainerInstance",
        "ecs:StartTelemetrySession",
        "ecs:Submit*",
        "ecr:GetAuthorizationToken",
        "ecr:BatchCheckLayerAvailability",
        "ecr:GetDownloadUrlForLayer",
        "ecr:BatchGetImage",
        "ecs:StartTask"
      ],
      "Resource": "*"
    }
  ]
}
EOF
}

resource "aws_iam_instance_profile" "compute" {
 # name = "mongo-profile"
  role = "${aws_iam_role.compute.name}"
}

# resource "aws_route53_record" "www" {
#   zone_id = "${var.zone_id}"
#   name = "infra.${var.base_domain}"
#   type = "A"
#   ttl = "300"
#   records = ["${aws_instance.compute.public_ip}"]
# }

resource "aws_security_group" "allow_cluster" {
  name_prefix = "sbir-one-${var.vpc_id}-"
  description = "Allow all traffic within cluster"
  vpc_id = "${var.vpc_id}"

  ingress {
    from_port = 0
    to_port = 0
    protocol = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    self = true
    from_port = 0
    protocol = "-1"
    to_port = 0
  }

  egress {
    from_port = 0
    to_port = 0
    protocol = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "SBIR-ONE allow cluster - ${terraform.workspace}"
  }

  # depends_on = ["aws_security_group.allow_lb_traffic"]
}

resource "aws_security_group" "allow_inbound" {
  count = "${terraform.workspace == "uat" ? 1 : 0}"
  name_prefix = "${var.vpc_id}-allow-inbound"
  description = "Allow inbound traffic"
  vpc_id = "${var.vpc_id}"

  ingress {
    from_port = 443
    to_port = 443
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port = 80
    to_port = 80
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port = 0
    to_port = 0
    protocol = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "SBIR ONE allow inbound traffic - ${terraform.workspace}"
    Terraform = "true"
    Project = "SBIR One"
    Environment = "${terraform.workspace}"
  }
}

resource "aws_security_group" "allow_ssh" {
  name_prefix = "${var.vpc_id}-"
  description = "Allow inbound SSH traffic for Bytecubed personel"
  vpc_id = "${var.vpc_id}"

  ingress {
    from_port = 22
    to_port = 22
    protocol = "tcp"
    cidr_blocks = ["${var.admin_cidrs}"]
  }

  egress {
    from_port = 0
    to_port = 0
    protocol = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "SBIR ONE allow ssh - ${terraform.workspace}"
    Terraform = "true"
    Project = "SBIR One"
    Environment = "${terraform.workspace}"
  }
}
