resource "aws_db_instance" "default" {
  depends_on             = ["aws_security_group.default"]
  identifier             = "${var.identifier}"
  allocated_storage      = "${var.storage}"
  engine                 = "${var.engine}"
  engine_version         = "${lookup(var.engine_version, var.engine)}"
  instance_class         = "${var.instance_class}"
  name                   = "${var.db_name}"
  username               = "${var.username}"
  password               = "${var.rds_password}"
  vpc_security_group_ids = ["${aws_security_group.default.id}"]
  skip_final_snapshot    = true
  db_subnet_group_name   = "${aws_db_subnet_group.default.name}"

  # publicly_accessible    = true
  # snapshot_identifier    = "some-snap"

  tags = {
    Environment = "${terraform.workspace}"
  }
}

resource "aws_db_subnet_group" "default" {
  name       = "rds-main"
  subnet_ids = "${var.public_subnets}"

  tags = {
    Name = "RDS Subnet Group"
  }
}

resource "aws_db_snapshot" "test" {
  db_instance_identifier = "${aws_db_instance.default.id}"
  db_snapshot_identifier = "sbir-one-reporting-db-snapshot"
}

// security groups
resource "aws_security_group" "default" {
  name        = "main_rds_sg"
  description = "Allow all inbound traffic on 5432"
  vpc_id      = "${var.vpc_id}"

  ingress {
    from_port = 5432
    to_port   = 5432
    protocol  = "TCP"
    # cidr_blocks = ["${var.cidr_blocks}"]
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${var.sg_name}"
  }
}
