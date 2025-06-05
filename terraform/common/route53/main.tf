resource "aws_route53_zone" "this" {
  name = var.domain_name
}

resource "aws_acm_certificate" "wildcard" {
  provider          = aws.us_east_1
  domain_name       = "*.${var.domain_name}"
  validation_method = var.validation_method
}

resource "aws_route53_record" "cert_validation" {
  zone_id = aws_route53_zone.this.zone_id
  name    = aws_acm_certificate.wildcard.domain_validation_options[0].resource_record_name
  type    = aws_acm_certificate.wildcard.domain_validation_options[0].resource_record_type
  records = [aws_acm_certificate.wildcard.domain_validation_options[0].resource_record_value]
  ttl     = 60
}

resource "aws_acm_certificate_validation" "wildcard" {
  provider                = aws.us_east_1
  certificate_arn         = aws_acm_certificate.wildcard.arn
  validation_record_fqdns = [aws_route53_record.cert_validation.fqdn]
}

resource "aws_route53_record" "subdomains" {
  for_each = var.subdomains

  zone_id = aws_route53_zone.this.zone_id
  name    = "${each.key}.${var.domain_name}"
  type    = var.recode_type

  alias {
    name                   = each.value.alb_dns_name
    zone_id                = each.value.alb_zone_id
    evaluate_target_health = true
  }
}
