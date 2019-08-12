resource "aws_security_group" "elasticsearch" {
  vpc_id = "${var.vpc_id}"

  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    protocol    = "-1"
    from_port   = 0
    to_port     = 0
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_elasticsearch_domain" "es" {
  domain_name           = "${terraform.workspace}-surs-${var.domain}"
  elasticsearch_version = "${var.es_version}"

  cluster_config {
    instance_type            = "${var.instance_type}"
    instance_count           = "${var.instance_count}"
    dedicated_master_enabled = "${var.instance_count >= var.dedicated_master_threshold ? true : false}"
    dedicated_master_count   = "${var.instance_count >= var.dedicated_master_threshold ? 3 : 0}"
    dedicated_master_type    = "${var.instance_count >= var.dedicated_master_threshold ? (var.dedicated_master_type != "false" ? var.dedicated_master_type : var.instance_type) : ""}"
    zone_awareness_enabled   = "${var.es_zone_awareness}"
  }

  ebs_options {
    ebs_enabled = true
    volume_size = "${var.ebs_volume_size}"
    volume_type = "${var.ebs_volume_type}"
  }

  //  vpc_options {
  //    subnet_ids = ["${var.public-subnets[0]}"]
  //    security_group_ids = ["${aws_security_group.elasticsearch.id}"]
  //  }

  # advanced_options {
  #   rest.action.multi.allow_explicit_index = true
  # }

  snapshot_options {
    automated_snapshot_start_hour = 23
  }
  tags = {
    Domain = "ElasticSearchEndpoint"
  }
}

resource "aws_elasticsearch_domain_policy" "main" {
  domain_name = "${terraform.workspace}-surs-${var.domain}"

  access_policies = <<CONFIG
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Action": ["es:*"],
            "Principal": {
                        "AWS": ["*"]
                        },
            "Effect": "Allow",
            "Resource": "${aws_elasticsearch_domain.es.arn}/*"
        }
    ]
}
CONFIG
}
