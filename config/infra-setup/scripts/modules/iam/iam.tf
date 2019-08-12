resource "aws_iam_role" "ecs_autoscale_role" {
  name = "${var.environment}_ecs_autoscale_role"

  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Service": "application-autoscaling.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
  }
  EOF
}

resource "aws_iam_role_policy" "ecs_autoscale_role_policy" {
  name = "${var.environment}_ecs_autoscale_role_policy"

  policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "ecs:DescribeServices",
        "ecs:UpdateService"
      ],
      "Resource": [
        "*"
      ]
    },
    {
      "Effect": "Allow",
      "Action": [
        "cloudwatch:DescribeAlarms"
      ],
      "Resource": [
        "*"
      ]
    }
  ]
}
EOF

  role = "${aws_iam_role.ecs_autoscale_role.id}"
}

resource "aws_iam_role" "ecs" {
  name = "${var.environment}_ecs"

  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "ec2.amazonaws.com"
      },
      "Effect": "Allow",
      "Sid": ""
    }
  ]
}
EOF
}

resource "aws_iam_policy_attachment" "ecs_for_ec2" {
  name       = "${var.environment}_ecs-for-ec2"
  roles      = ["${aws_iam_role.ecs.id}"]
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonEC2ContainerServiceforEC2Role"
}

resource "aws_iam_policy" "s3_read_only_policy" {
  name = "${var.environment}_s3_read_only_policy"
  path = "/"

  policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
   {
     "Sid":"AddPerm",
     "Effect":"Allow",
     "Action":["s3:GetObject"],
     "Resource":["arn:aws:s3:::bytecubed-ecs-infra/*"]
   }
 ]
}
EOF
}

resource "aws_iam_policy_attachment" "read_only_s3" {
  name       = "${var.environment}_read_only_s3"
  roles      = ["${aws_iam_role.ecs.id}"]
  policy_arn = "${aws_iam_policy.s3_read_only_policy.arn}"
}

resource "aws_iam_policy" "s3_backup_access_policy" {
  name = "${var.environment}_s3_backup_access_policy"
  path = "/"

  policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
   {
     "Sid":"AddPerm",
     "Effect":"Allow",
     "Action":["s3:GetObject", "s3:PutObject"],
     "Resource":["arn:aws:s3:::bytecubed-backups/*"]
   }
 ]
}
EOF
}

resource "aws_iam_policy_attachment" "backup_bucket_s3" {
  name       = "${var.environment}_backup_bucket_s3"
  roles      = ["${aws_iam_role.ecs.id}"]
  policy_arn = "${aws_iam_policy.s3_backup_access_policy.arn}"
}

resource "aws_iam_role" "ecs_lb" {
  name = "${var.environment}_ecs-lb"

  assume_role_policy = <<EOF
{
  "Version": "2008-10-17",
  "Statement": [
    {
      "Sid": "",
      "Effect": "Allow",
      "Principal": {
        "Service": "ecs.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
EOF
}

resource "aws_iam_role_policy_attachment" "ecs_lb" {
  role       = "${aws_iam_role.ecs_lb.name}"
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonEC2ContainerServiceRole"
}

data "aws_iam_policy_document" "ecs_service_role" {
  statement {
    effect  = "Allow"
    actions = ["sts:AssumeRole"]

    principals {
      type        = "Service"
      identifiers = ["ecs.amazonaws.com"]
    }
  }
}

resource "aws_iam_role" "ecs_role" {
  name               = "${var.environment}_ecs_role"
  assume_role_policy = "${data.aws_iam_policy_document.ecs_service_role.json}"
}

data "aws_iam_policy_document" "ecs_service_policy" {
  statement {
    effect    = "Allow"
    resources = ["*"]

    actions = [
      "elasticloadbalancing:Describe*",
      "elasticloadbalancing:DeregisterInstancesFromLoadBalancer",
      "elasticloadbalancing:RegisterInstancesWithLoadBalancer",
      "ec2:Describe*",
      "ecs:RegisterContainerInstance",
      "ec2:AuthorizeSecurityGroupIngress",
      "elasticfilesystem:*",
    ]
  }
}

/* ecs service scheduler role */
resource "aws_iam_role_policy" "ecs_service_role_policy" {
  name   = "${var.environment}_ecs_service_role_policy"
  policy = "${data.aws_iam_policy_document.ecs_service_policy.json}"
  role   = "${aws_iam_role.ecs_role.id}"
}

/* role that the Amazon ECS container agent and the Docker daemon can assume */
resource "aws_iam_role" "ecs_execution_role" {
  name = "${var.environment}_ecs_task_execution_role"

  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "",
      "Effect": "Allow",
      "Principal": {
        "Service": "ecs-tasks.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
  }
  EOF
}

resource "aws_iam_role_policy" "ecs_execution_role_policy" {
  name = "${var.environment}_ecs_execution_role_policy"

  policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "ecr:GetAuthorizationToken",
        "ecr:BatchCheckLayerAvailability",
        "ecr:GetDownloadUrlForLayer",
        "ecr:BatchGetImage",
        "logs:CreateLogStream",
        "ec2:Describe*",
        "logs:PutLogEvents",
        "ecs:*",
        "elasticfilesystem:*",
        "ecs:RegisterContainerInstance"
      ],
      "Resource": "*"
    }
  ]
}
EOF

  role = "${aws_iam_role.ecs_execution_role.id}"
}

resource "aws_iam_instance_profile" "ecs" {
  name = "${var.environment}_ecs_profile"
  role = "${aws_iam_role.ecs.name}"
}
