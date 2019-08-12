# output "dns_name" {
#   value = "${aws_instance.neo4j.public_ip}"
# }

output "log_forward_lambda_arn" {
  value = "${aws_lambda_function.cwl_stream_lambda.arn}"
}
