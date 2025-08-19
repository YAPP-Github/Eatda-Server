output "vpc_id" {
  description = "The ID of the VPC"
  value       = module.vpc.vpc_id
}

output "vpc_cidr_block" {
  description = "The CIDR block of the VPC"
  value       = module.vpc.vpc_cidr_block
}

output "availability_zones" {
  description = "List of availability zones"
  value       = module.vpc.availability_zones
}

output "public_subnet_ids" {
  description = "Map of public subnet IDs for environments"
  value = {
    dev  = module.vpc.public_subnet_ids.dev
    prod = module.vpc.public_subnet_ids.prod
  }
}

output "private_subnet_ids" {
  description = "List of IDs of private subnets"
  value = {
    dev  = module.vpc.private_subnet_ids.dev
    prod = module.vpc.private_subnet_ids.prod
  }
  sensitive = true
}

output "security_group_ids" {
  value     = module.security_group.security_group_ids
  sensitive = true
}

output "target_group_arns" {
  value = module.alb.target_group_arns
}

output "target_group_names" {
  value = module.alb.target_group_names
}

output "user_name" {
  value     = module.iam.user_name
  sensitive = true
}

output "role_name" {
  value = {
    for k, mod in module.iam_role :
    k => mod.role_name
  }
  sensitive = true
}

output "role_arn" {
  value = {
    for k, mod in module.iam_role :
    k => mod.role_arn
  }
  sensitive = true
}

output "instance_profile_name" {
  value = {
    for k, mod in module.iam_role :
    k => mod.instance_profile_name
  }
}

output "public_route_table_id" {
  description = "ID of the public route table"
  value       = module.vpc.public_route_table_id
  sensitive   = true
}

output "private_route_table_ids" {
  description = "List of IDs of private route tables"
  value       = module.vpc.private_route_table_ids
  sensitive   = true
}
