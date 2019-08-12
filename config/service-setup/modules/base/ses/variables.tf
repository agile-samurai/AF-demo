variable "domain" {
  description = "The domain to have included as a valid domain (with DKIM set up) in SES"
}

variable "zone_id" {
  description = "Route 53 zone ID to be used in doing domain verfication for SES"
}

variable "dkim_ttl" {
  description = "The TTL for the DKIM records created in Route 53 for SES"
  default     = "600"
}

variable "ses_ttl" {
  description = "The TTL for the verification records created in Route 53 for SES"
  default     = "300"
}
