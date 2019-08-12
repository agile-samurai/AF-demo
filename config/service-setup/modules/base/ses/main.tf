resource "aws_ses_domain_identity" "ses-domain-identity" {
  domain = "${var.domain}"
}

resource "aws_ses_domain_dkim" "ses-domain-dkim" {
  domain = "${aws_ses_domain_identity.ses-domain-identity.domain}"
}

// Note: these must be uncommented and allowed to run once per AWS account - they create DNS records that are permanent,
// and as of the time of writing this, Terraform does not support checking if those records exist via a data source:
// https://github.com/terraform-providers/terraform-provider-aws/issues/5147


//resource "aws_route53_record" "domain_amazonses_verification_record" {
//  zone_id = "${var.zone_id}"
//  name    = "_amazonses.${var.domain}"
//  type    = "TXT"
//  ttl     = "${var.ses_ttl}"
//  records = ["${aws_ses_domain_identity.ses-domain-identity.verification_token}"]
//}
//
//resource "aws_route53_record" "amazonses_verification_record" {
//  count   = 3
//  zone_id = "${var.zone_id}"
//  name    = "${element(aws_ses_domain_dkim.ses-domain-dkim.dkim_tokens, count.index)}._domainkey.${var.domain}"
//  type    = "CNAME"
//  ttl     = "${var.dkim_ttl}"
//  records = ["${element(aws_ses_domain_dkim.ses-domain-dkim.dkim_tokens, count.index)}.dkim.amazonses.com"]
//}

