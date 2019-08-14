data "aws_availability_zones" "available" {}

### Create VPC
resource "aws_vpc" "main" {
  cidr_block           = "${var.cidr_block}"
  enable_dns_support   = true
  enable_dns_hostnames = true

  tags = {
    Name = "${var.environment}_infra_vpc"
  }
}

resource "aws_subnet" "main" {
  count  = "${var.az_count}"
  vpc_id = "${aws_vpc.main.id}"

  availability_zone       = "${data.aws_availability_zones.available.names[count.index]}"
  cidr_block              = "${cidrsubnet(aws_vpc.main.cidr_block, 8, var.az_count + count.index)}"
  depends_on              = ["aws_internet_gateway.main"]
  map_public_ip_on_launch = true

  tags = {
    Name = "${var.environment}_subnet_public"
  }
}

resource "aws_subnet" "private" {
  count             = "${var.az_count}"
  vpc_id            = "${aws_vpc.main.id}"
  availability_zone = "${data.aws_availability_zones.available.names[count.index]}"
  cidr_block        = "${cidrsubnet(aws_vpc.main.cidr_block, 8, count.index)}"
  depends_on        = ["aws_internet_gateway.main"]

  tags = {
    Name = "${var.environment}_subnet_private"
  }
}

resource "aws_internet_gateway" "main" {
  vpc_id = "${aws_vpc.main.id}"

  tags = {
    Name = "${var.environment}_ig"
  }
}

# Route the public subnet traffic through the IGW
resource "aws_route" "internet_access" {
  route_table_id         = "${aws_vpc.main.main_route_table_id}"
  destination_cidr_block = "0.0.0.0/0"
  gateway_id             = "${aws_internet_gateway.main.id}"
}
