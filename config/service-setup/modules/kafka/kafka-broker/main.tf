//
// ECS - kafka broker
//
data "aws_ecr_repository" "kafka-ecr" {
  name = "${var.repository_name}"
}

//resource "aws_ecr_lifecycle_policy" "kafka-ecr-lifecycle-policy" {
//  repository = "${data.aws_ecr_repository.kafka-ecr.name}"
//  policy     = "${file("${path.module}/ecr-lifecycle-policy.json")}"
//  depends_on = ["data.aws_ecr_repository.kafka-ecr"]
//} #TODO put this back later

resource "aws_cloudwatch_log_group" "ecs-kafkabroker" {
  name              = "${var.environment}-kafkabroker"
  retention_in_days = 7

  tags = {
    Environment = "${var.environment}"
    Application = "Kafka Broker Module"
  }
}

data "aws_ecs_task_definition" "kafkabroker" {
  task_definition = "${aws_ecs_task_definition.kafkabroker.family}"
  depends_on      = ["aws_ecs_task_definition.kafkabroker"]
}

data "template_file" "kafkabroker_task" {
  template = "${file("${path.module}/kafka_task.json")}"

  vars = {
    name              = "${var.name}"
    image             = "wurstmeister/kafka:latest"
    log_group         = "${aws_cloudwatch_log_group.ecs-kafkabroker.name}"
    env               = "${var.environment}"
    region            = "${var.region}"
    kafka_internal_ip = "${var.kafka_internal_ip}"
  }

  depends_on = ["aws_cloudwatch_log_group.ecs-kafkabroker"]
}

resource "aws_ecs_task_definition" "kafkabroker" {
  family                = "${var.environment}_kafkabroker"
  container_definitions = "${data.template_file.kafkabroker_task.rendered}"

  execution_role_arn = "${var.ecs_task_execution_role_arn}"
  task_role_arn      = "${var.ecs_task_execution_role_arn}"

  depends_on = [
    "data.template_file.kafkabroker_task",
  ]
}

resource "aws_ecs_service" "kafkabroker" {
  name = "${var.name}"

  desired_count = "${var.service_desired_count}"

  cluster = "${var.ecs_cluster_id}"

  task_definition = "${aws_ecs_task_definition.kafkabroker.family}:${max("${aws_ecs_task_definition.kafkabroker.revision}", "${data.aws_ecs_task_definition.kafkabroker.revision}")}"

  service_registries {
    registry_arn   = "${var.discovery_service_arn}"
    container_name = "${var.environment}-${var.name}"
    container_port = 9092
  }

  ordered_placement_strategy {
    type  = "spread"
    field = "attribute:ecs.availability-zone"
  }

  ordered_placement_strategy {
    type  = "binpack"
    field = "memory"
  }

  depends_on = ["aws_ecs_task_definition.kafkabroker"]
}
