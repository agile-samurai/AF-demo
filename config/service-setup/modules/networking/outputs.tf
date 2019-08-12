output "public_subnets" {
  value = "${aws_subnet.main.*.id}"
}

output "private_subnets" {
  value = "${aws_subnet.private.*.id}"
}

output "vpc_id" {
  value = "${aws_vpc.main.id}"
}
