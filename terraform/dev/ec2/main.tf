resource "aws_instance" "dev" {
  ami                         = var.instance_definitions.ami
  instance_type               = var.instance_definitions.instance_type
  subnet_id                   = lookup(var.instance_subnet_map, var.instance_definitions.role)
  iam_instance_profile = var.instance_definitions.iam_instance_profile
  key_name = var.instance_definitions.key_name
  user_data = var.instance_definitions.user_data
  vpc_security_group_ids = [var.ec2_sg_id]
  user_data_replace_on_change = true

  tags = {
    Name = "${var.name_prefix}-${var.instance_definitions.role}"
  }
}