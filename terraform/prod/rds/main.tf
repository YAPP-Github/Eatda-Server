resource "aws_subnet" "rds_private" {
  count             = 2
  vpc_id            = var.vpc_id
  cidr_block        = var.rds_subnet_cidrs[count.index]
  availability_zone = var.availability_zones[count.index]

  tags = merge(var.tags, {
    Name = "${var.identifier}-rds-private-${count.index + 1}"
  })
}

resource "aws_db_subnet_group" "private" {
  name       = "${var.identifier}-subnet-group"
  subnet_ids = aws_subnet.rds_private[*].id

  tags = merge(var.tags, {
    Name = "${var.identifier}-subnet-group"
  })
}

resource "random_pet" "rds_user_name" {
  length    = 2
  separator = "_"
}

resource "random_password" "rds_password" {
  length  = 16
  special = true
}

resource "aws_ssm_parameter" "mysql_user_name" {
  name        = "/prod/MYSQL_USER_NAME"
  type        = "SecureString"
  value       = random_pet.rds_user_name.id
  description = "Generated MySQL user name for prod RDS"
  overwrite   = true
}

resource "aws_ssm_parameter" "mysql_password" {
  name        = "/prod/MYSQL_PASSWORD"
  type        = "SecureString"
  value       = random_password.rds_password.result
  description = "Generated MySQL user name for prod RDS"
  overwrite   = true
}

resource "aws_db_instance" "prod" {
  identifier              = var.identifier
  engine                  = var.engine
  engine_version          = var.engine_version
  instance_class          = var.instance_class
  allocated_storage       = var.allocated_storage
  db_name                 = var.db_name
  username                = random_pet.rds_user_name.id
  password                = random_password.rds_password.result
  vpc_security_group_ids  = var.vpc_security_group_ids
  db_subnet_group_name    = aws_db_subnet_group.private.name
  multi_az                = var.multi_az
  backup_retention_period = var.backup_retention_period
  storage_encrypted       = var.storage_encrypted
  skip_final_snapshot     = true

  tags = var.tags
}
