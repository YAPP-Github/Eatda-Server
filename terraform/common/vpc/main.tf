resource "aws_vpc" "timeeat_vpc" {
  cidr_block                           = var.vpc_cidr
  enable_dns_hostnames                 = var.enable_dns_hostnames
  enable_dns_support                   = var.enable_dns_support
  enable_network_address_usage_metrics = var.enable_network_address_usage_metrics

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-vpc"
  })
}

resource "aws_subnet" "timeeat_public_subnet" {
  count = length(var.public_subnet_cidrs)
  vpc_id                  = aws_vpc.timeeat_vpc.id
  cidr_block              = var.public_subnet_cidrs[count.index]
  availability_zone       = var.availability_zones[count.index]
  map_public_ip_on_launch = true

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-pub-sub-${substr(var.availability_zones[count.index], -2, 2)}"
  })
}

resource "aws_subnet" "timeeat_private_subnet" {
  count = length(var.private_subnet_cidrs)
  vpc_id            = aws_vpc.timeeat_vpc.id
  cidr_block        = var.private_subnet_cidrs[count.index]
  availability_zone = var.availability_zones[count.index]

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-pri-sub-${substr(var.availability_zones[count.index], -2, 2)}"
  })
}

resource "aws_internet_gateway" "timeeat_igw" {
  vpc_id = aws_vpc.timeeat_vpc.id

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-igw"
  })
}

resource "aws_route_table" "timeeat_public_rt" {
  vpc_id = aws_vpc.timeeat_vpc.id

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-rt-pub"
  })
}

resource "aws_route_table" "timeeat_private_rt" {
  count = length(var.private_subnet_cidrs)
  vpc_id = aws_vpc.timeeat_vpc.id

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-rt-pri-${substr(var.availability_zones[count.index], -2, 2)}"
  })
}

resource "aws_route_table_association" "timeeat_public_rta" {
  count = length(var.public_subnet_cidrs)
  subnet_id      = aws_subnet.timeeat_public_subnet[count.index].id
  route_table_id = aws_route_table.timeeat_public_rt.id
}

resource "aws_route_table_association" "timeeat_private_rta" {
  count = length(var.private_subnet_cidrs)
  subnet_id      = aws_subnet.timeeat_private_subnet[count.index].id
  route_table_id = aws_route_table.timeeat_private_rt[count.index].id
}

resource "aws_route" "timeeat_public_internet_gateway" {
  route_table_id         = aws_route_table.timeeat_public_rt.id
  destination_cidr_block = "0.0.0.0/0"
  gateway_id             = aws_internet_gateway.timeeat_igw.id
}