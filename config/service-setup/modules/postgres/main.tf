resource "aws_db_instance" "postgres" {
  allocated_storage   = 40
  storage_type        = "gp2"
  engine              = "postgres"
  engine_version      = "9.6.9"
  instance_class      = "db.t2.micro"
  name                = "${var.db_name}"
  username            = "${var.db_username}"
  password            = "${var.db_password}"
  skip_final_snapshot = true

  # parameter_group_name = "postgres-9.6.9"
}
