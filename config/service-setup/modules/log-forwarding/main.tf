resource "aws_iam_role" "lambda_elasticsearch_execution_role" {
  # name = "lambda_elasticsearch_execution_role"

  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "lambda.amazonaws.com"
      },
      "Effect": "Allow"
    }
  ]
}
EOF
}

resource "aws_iam_role_policy" "lambda_elasticsearch_execution_policy" {
  # name = "lambda_elasticsearch_execution_policy"
  role = "${aws_iam_role.lambda_elasticsearch_execution_role.id}"

  policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "logs:CreateLogGroup",
        "logs:CreateLogStream",
        "logs:PutLogEvents"
      ],
      "Resource": [
        "arn:aws:logs:*:*:*"
      ]
    },
    {
      "Effect": "Allow",
      "Action": "es:ESHttpPost",
      "Resource": "arn:aws:es:*:*:*"
    }
  ]
}
EOF
}

resource "aws_lambda_function" "cwl_stream_lambda" {
  filename         = "./logging/cwl2eslambda.zip"
  function_name    = "LogsToElasticsearch-${var.container_family}"
  role             = "${aws_iam_role.lambda_elasticsearch_execution_role.arn}"
  handler          = "cwl2es.handler"
  source_code_hash = "${filesha256("./logging/cwl2eslambda.zip")}"
  runtime          = "nodejs8.10"

  environment {
    variables = {
      es_endpoint = "${var.es_endpoint}"
    }
  }
}
