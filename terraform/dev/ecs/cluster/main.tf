resource "aws_ecs_cluster" "ecs_dev_cluster" {
  name = var.cluster_name

  setting {
    name  = var.cluster_settings.name
    value = var.cluster_settings.value
  }

  tags = {
    Name = var.cluster_name
  }
}