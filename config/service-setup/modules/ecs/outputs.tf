output "ecs_cluster_id" {
  value = "${aws_ecs_cluster.infrastructure.id}"
}

output "ecs_cluster_name" {
  value = "${aws_ecs_cluster.infrastructure.name}"
}

output "dns_name" {
  value = "${aws_instance.compute.public_ip}"
}

output "sg-allow-ssh" {
  value = "${aws_security_group.allow_ssh.id}"
}

output "sg-allow-cluster" {
  value = "${aws_security_group.allow_cluster.id}"
}
