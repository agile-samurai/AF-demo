# Simply specify the family to find the latest ACTIVE revision in that family.
data "aws_ecs_task_definition" "mongo" {
  task_definition = "${aws_ecs_task_definition.mongo.family}"
}

resource "aws_ecs_task_definition" "mongo" {
  family = "mongo"

  volume {
    name      = "mongo-storage"
    host_path = "/efs"
  }

  container_definitions = <<DEFINITION
[
  {
    "cpu": 512,
    "essential": true,
    "image": "mongo:latest",
    "memory": 1024,
    "memoryReservation": 64,
    "name": "mongo",
    "mountPoints": [
      {
        "sourceVolume": "mongo-storage",
        "containerPath": "/data/db"
      }
    ],
    "portMappings": [
      {
        "containerPort": 27017,
        "hostPort": 27017
      }
    ],
    "environment" : [
      { "name": "MONGO_INITDB_ROOT_USERNAME", "value" :"${var.MONGO_INITDB_ROOT_USERNAME}"},
      { "name": "MONGO_INITDB_ROOT_PASSWORD", "value" :"${var.MONGO_INITDB_ROOT_PASSWORD}"}
    ]
  }
]
DEFINITION
}

resource "aws_ecs_service" "mongo" {
  name          = "mongo"
  cluster       = "${var.cluster_id}"
  desired_count = 1

  # ECS limitation that needs to be addressed.
  # tags = {
  #   Environment = "${terraform.workspace}"
  # }

  # Track the latest ACTIVE revision
  task_definition = "${aws_ecs_task_definition.mongo.family}:${max("${aws_ecs_task_definition.mongo.revision}", "${data.aws_ecs_task_definition.mongo.revision}")}"
}
