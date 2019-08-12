resource "aws_iam_role" "ecs_autoscale_role" {
  assume_role_policy = "${file("${path.module}/policies/ecs-autoscale-role.json")}"
}

resource "aws_iam_role_policy" "ecs_autoscale_role_policy" {
  policy = "${file("${path.module}/policies/ecs-autoscale-role-policy.json")}"
  role   = "${aws_iam_role.ecs_autoscale_role.id}"
}

resource "aws_iam_role" "ecs_instance_role" {

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

resource "aws_iam_instance_profile" "ecs" {
  role = "${aws_iam_role.ecs_instance_role.name}"
}

resource "aws_iam_role_policy" "ecs_instance_role_policy" {
  name = "${var.environment}_ecs_instance_role_policy"
  role = "${aws_iam_role.ecs_instance_role.id}"

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
        "ecs:StartTask"
      ],
      "Resource": [
        "*"
      ]
    }
  ]
}
EOF
}

resource "aws_iam_role" "ecs_service_role" {
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

resource "aws_iam_role_policy" "ecs_service_role_policy" {
  name = "${var.environment}-ecs_service_role"
  role = "${aws_iam_role.ecs_service_role.id}"

  policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "elasticloadbalancing:Describe*",
        "elasticloadbalancing:DeregisterTargets",
        "elasticloadbalancing:RegisterTargets",
        "elasticloadbalancing:RegisterInstancesWithLoadBalancer",
        "elasticloadbalancing:DeregisterInstancesFromLoadBalancer",
        "ec2:Describe*",
        "ec2:AuthorizeSecurityGroupIngress"
      ],
      "Resource": [
        "*"
      ]
    }
  ]
}
EOF
}

/* role that the Amazon ECS container agent and the Docker daemon can assume */
resource "aws_iam_role" "ecs_execution_role" {
  assume_role_policy = "${file("${path.module}/policies/ecs-task-execution-role.json")}"
}

resource "aws_iam_role_policy" "ecs_execution_role_policy" {
  policy = "${file("${path.module}/policies/ecs-execution-role-policy.json")}"
  role   = "${aws_iam_role.ecs_execution_role.id}"
}

resource "aws_iam_role_policy" "nessus_s3_role_policy" {
  role = "${aws_iam_role.ecs_instance_role.id}"

  policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
              "s3:*"
            ],
            "Resource": [
              "arn:aws:s3:::sbir-one-nessus",
              "arn:aws:s3:::sbir-one-nessus/*"
            ]
        }
    ]
}
EOF

  depends_on = ["aws_iam_role.ecs_instance_role"]
}
