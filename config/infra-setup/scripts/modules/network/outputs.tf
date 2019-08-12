output "public_subnets" {
  value = "${aws_subnet.main.*.id}"
}

output "private_subnets" {
  value = "${aws_subnet.private.*.id}"
}

output "vpc_id" {
  value = "${aws_vpc.main.id}"
}

output "sg_ssh_id" {
  value = "${aws_security_group.allow_ssh_default.id}"
}

output "sg_80_id" {
  value = "${aws_security_group.allow_all_80_default.id}"
}

output "sg_443_id" {
  value = "${aws_security_group.allow_all_443_default.id}"
}

output "sg_8080_id" {
  value = "${aws_security_group.allow_all_8080_default.id}"
}

output "sg_mysql_id" {
  value = "${aws_security_group.allow_mysql.id}"
}

output "sg_ecs_tasks_id" {
  value = "${aws_security_group.ecs_tasks.id}"
}

//elk - will further refine once elk + grafana is running succesfully within shared infra account

output "sg-allow-ssh" {
  value = "${aws_security_group.allow_ssh.id}"
}

output "sg-allow-cluster" {
  value = "${aws_security_group.allow_cluster.id}"
}

output "sg-allow-inbound" {
  value = "${aws_security_group.allow_all_inbound.id}"
}
output "cidr_block" {
  value = "${aws_vpc.main.cidr_block}"
}