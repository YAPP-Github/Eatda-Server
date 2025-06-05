module "timeeat_vpc" {
  source = "./vpc"
}

module "timeeat_iam" {
  source      = "./iam"
  group_name  = local.group_name
  policy_arns = local.policy_arns
  user_names  = local.user
}

module "timeeat_iam_role" {
  source   = "iam-role"
  for_each = local.iam_roles

  name                 = each.key
  assume_role_services = each.value.assume_role_services
  policy_arns          = each.value.policy_arns
}

module "security_group" {
  source   = "./security-group"
  for_each = local.security_group_defs

  name        = each.value.name
  description = each.value.description
  vpc_id      = module.timeeat_vpc.vpc_id

  ingress_rules = each.value.ingress
  egress_rules  = local.all_egress
}

module "timeeat_route53" {
  source            = "./route53"
  domain_name       = local.domain_name
  validation_method = local.validation_method
  recode_type       = local.recode_type
  subdomains        = local.subdomains
}

module "timeeat_alb" {
  source  = "./alb"
  vpc_id  = module.timeeat_vpc.vpc_id
  subnets = module.timeeat_vpc.public_subnet_ids
  alb_security_group_id = [module.security_group.security_group_ids["alb"]]
}
