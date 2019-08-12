//
// ECS - zookeeper
//
data "aws_ecr_repository" "zookeeper-ecr" {
  name = "${var.repository_name}"
}

//resource "aws_ecr_lifecycle_policy" "zookeeper-ecr-lifecycle-policy" {
//  repository = "${data.aws_ecr_repository.zookeeper-ecr.name}"
//  policy     = "${file("${path.module}/ecr-lifecycle-policy.json")}"
//  depends_on = ["data.aws_ecr_repository.zookeeper-ecr"]
//} #TODO put this back later

resource "aws_cloudwatch_log_group" "ecs-zookeeper" {
  name              = "${var.environment}-zookeeper"
  retention_in_days = 7

  tags = {
    Environment = "${var.environment}"
    Application = "Zookeeper Module"
  }
}

data "aws_ecs_task_definition" "zookeeper" {
  task_definition = "${aws_ecs_task_definition.zookeeper.family}"
  depends_on      = ["aws_ecs_task_definition.zookeeper"]
}

data "template_file" "zookeeper_task" {
  template = "${file("${path.module}/zookeeper_task.json")}"

  vars = {
    name      = "${var.name}"
    image     = "wurstmeister/kafka"
    log_group = "${aws_cloudwatch_log_group.ecs-zookeeper.name}"
    env       = "${var.environment}"
    region    = "${var.region}"
  }

  depends_on = ["aws_cloudwatch_log_group.ecs-zookeeper"]
}

resource "aws_ecs_task_definition" "zookeeper" {
  family                = "${var.environment}_zookeeper"
  container_definitions = "${data.template_file.zookeeper_task.rendered}"

  execution_role_arn = "${var.ecs_task_execution_role_arn}"
  task_role_arn      = "${var.ecs_task_execution_role_arn}"

  depends_on = [
    "data.template_file.zookeeper_task",
  ]
}

resource "aws_ecs_service" "zookeeper" {
  name = "${var.name}"

  desired_count   = "${var.service_desired_count}"
  cluster         = "${var.ecs_cluster_id}"
  task_definition = "${aws_ecs_task_definition.zookeeper.family}:${max("${aws_ecs_task_definition.zookeeper.revision}", "${data.aws_ecs_task_definition.zookeeper.revision}")}"

  /* iam_role        = "${var.ecs_service_role_arn}" note: when this was in place, got
  the following error: "InvalidParameterException: You cannot specify an IAM role for services that
  require a service linked role." */

  # service_registries {
  #   registry_arn   = "${var.discovery_service_arn}"
  #   container_name = "${var.environment}-${var.name}"
  #   container_port = 2181
  # }
  ordered_placement_strategy {
    type  = "spread"
    field = "attribute:ecs.availability-zone"
  }
  ordered_placement_strategy {
    type  = "binpack"
    field = "memory"
  }
  depends_on = ["aws_ecs_task_definition.zookeeper"]
}
