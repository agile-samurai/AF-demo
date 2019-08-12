resource "aws_alb" "uat-alb" {
  count              = "${var.environment == "uat" ? 1 : 0}"
  name               = "${var.environment}-alb"
  subnets            = ["${var.public_subnets}"]
  load_balancer_type = "application"
  internal           = false
  idle_timeout       = 300
  security_groups    = ["${aws_security_group.allow_inbound.id}", "${aws_security_group.allow_ssh.id}"]

  //  enable_deletion_protection = true

  tags {
    Name        = "Sbir-One ${var.environment} ALB"
    Terraform   = "true"
    Project     = "${var.project}"
    Environment = "${local.environment}"

    //    Version = "${var.version}"
  }
  depends_on = ["aws_security_group.allow_lb_traffic", "aws_security_group.allow_ssh"]
}

resource "aws_alb" "alb" {
  count              = "${var.environment == "uat" ? 0 : 1}"
  name               = "${var.environment}-alb"
  subnets            = ["${var.public_subnets}"]
  load_balancer_type = "application"
  internal           = false
  idle_timeout       = 300
  security_groups    = ["${aws_security_group.allow_lb_traffic.id}", "${aws_security_group.allow_lb_traffic2.id}", "${aws_security_group.allow_ssh.id}"]

  //  enable_deletion_protection = true

  tags {
    Name        = "Sbir-One ${var.environment} ALB"
    Terraform   = "true"
    Project     = "${var.project}"
    Environment = "${local.environment}"

    //    Version = "${var.version}"
  }
  depends_on = ["aws_security_group.allow_lb_traffic", "aws_security_group.allow_ssh"]
}

module "ses" {
  source  = "./ses"
  domain  = "sbir-one.bytecubed.io"
  zone_id = "${var.zone_id}"
}

module "bulk-upload-template-s3-object" {
  source       = "./s3"
  bucketname   = "sbir-one-${var.environment}-assets"
  localFile    = "base/s3/assets/TopicDatabaseTemplate.mdb"
  fileNameInS3 = "static/TopicDatabaseTemplate.mdb"
}

module "phase-I-tech-volume-s3-object" {
  source       = "./s3"
  bucketname   = "sbir-one-${var.environment}-assets"
  localFile    = "base/s3/assets/TEMPLATE_SBIR_STTR_Phase_I_Tech_Volume.docx"
  fileNameInS3 = "static/TEMPLATE_SBIR_STTR_Phase_I_Tech_Volume.docx"
}

module "cost-breakdown-guidance-s3-object" {
  source       = "./s3"
  bucketname   = "sbir-one-${var.environment}-assets"
  localFile    = "base/s3/assets/cost-breakdown-guidance.pdf"
  fileNameInS3 = "static/cost-breakdown-guidance.pdf"
}

module "guidance-for-completing-the-ccr-s3-object" {
  source       = "./s3"
  bucketname   = "sbir-one-${var.environment}-assets"
  localFile    = "base/s3/assets/guidance-for-completing-the-ccr.pdf"
  fileNameInS3 = "static/guidance-for-completing-the-ccr.pdf"
}

resource "aws_vpc_peering_connection" "to_infra" {
  peer_owner_id = "${var.peer_owner_id}"
  peer_vpc_id   = "${var.peer_vpc_id}"

  vpc_id = "${var.vpc_id}"

  tags = {
    Name = "peer_to_infra"
  }

  #depends_on = ["${module.network}"]
}
