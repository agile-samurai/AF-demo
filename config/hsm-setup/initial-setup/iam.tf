resource "aws_iam_user" "admin-user" {
  name          = "admin"
  path          = "/"
  force_destroy = true
}

resource "aws_iam_user_login_profile" "admin" {
  user    = aws_iam_user.admin-user.name
  pgp_key = "keybase:odwayne"
}

resource "aws_iam_group" "admin-group" {
  name = "admins"
}

resource "aws_iam_group" "user-group" {
  name = "CloudHsmReadOnlyUsers"
}

resource "aws_iam_policy" "admin-policy" {
  name        = "admin-policy"
  description = "A test admin policy"
  policy      = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": "*",
      "Resource": "*"
    }
  ]
}
EOF
}

resource "aws_iam_policy" "hsm-policy" {
  name        = "HSMAdminPolicy"
  description = "HSM policy for administrators"
  policy      = <<EOF
{
   "Version":"2012-10-17",
   "Statement":{
      "Effect":"Allow",
      "Action":[
         "cloudhsm:*",
         "ec2:CreateNetworkInterface",
         "ec2:DescribeNetworkInterfaces",
         "ec2:DescribeNetworkInterfaceAttribute",
         "ec2:DetachNetworkInterface",
         "ec2:DeleteNetworkInterface",
         "ec2:CreateSecurityGroup",
         "ec2:AuthorizeSecurityGroupIngress",
         "ec2:AuthorizeSecurityGroupEgress",
         "ec2:RevokeSecurityGroupEgress",
         "ec2:DescribeSecurityGroups",
         "ec2:DeleteSecurityGroup",
         "ec2:CreateTags",
         "ec2:DescribeVpcs",
         "ec2:DescribeSubnets",
         "iam:CreateServiceLinkedRole"
      ],
      "Resource":"*"
   }
}
EOF
}

resource "aws_iam_group_policy_attachment" "group-admin-policy" {
  group      = aws_iam_group.admin-group.name
  policy_arn = aws_iam_policy.admin-policy.arn
}

resource "aws_iam_group_policy_attachment" "group-hsm-policy" {
  group      = aws_iam_group.admin-group.name
  policy_arn = aws_iam_policy.hsm-policy.arn
}