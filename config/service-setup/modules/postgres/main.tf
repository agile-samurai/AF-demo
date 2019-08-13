resource "aws_db_instance" "postgres" {
  allocated_storage   = 40
  storage_type        = "gp2"
  engine              = "postgres"
  engine_version      = "9.6.9"
  instance_class      = "db.t2.micro"
  name                = "awardreporting"
  username            = "${var.db_user}"
  password            = "${var.db_pass}"
  skip_final_snapshot = true

  # parameter_group_name = "postgres-9.6.9"
}
