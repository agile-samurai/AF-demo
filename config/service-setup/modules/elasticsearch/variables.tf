variable "vpc_id" {}

variable "vpc_cidr" {}

variable "public-subnets" {
  type        = "list"
  description = "public subnets used in vpc"
}


variable "region" {}

variable "domain" {
  description = "Domain name for Elasticsearch cluster"
  default     = "sbirone-es"
}

variable "es_version" {
  description = "Version of Elasticsearch to deploy (default 5.1)"
  default     = "6.4"
}

variable "instance_type" {
  description = "ES instance type for data nodes in the cluster (default t2.small.elasticsearch)"
  default     = "t2.small.elasticsearch"
}

variable "instance_count" {
  description = "Number of data nodes in the cluster (default 6)"
  default     = 3
}

variable "dedicated_master_type" {
  description = "ES instance type to be used for dedicated masters (default same as instance_type)"
  default     = false
}

variable "es_zone_awareness" {
  description = "Enable zone awareness for Elasticsearch cluster (default false)"
  default     = "false"
}

variable "tags" {
  description = "tags to apply to all resources"
  type        = "map"
  default     = {}
}

variable "use_prefix" {
  description = "Flag indicating whether or not to use the domain_prefix. Default: true"
  default     = true
}

variable "domain_prefix" {
  description = "String to be prefixed to search domain. Default: tf-"
  default     = "tf-"
}

variable "dedicated_master_threshold" {
  description = "The number of instances above which dedicated master nodes will be used. Default: 10"
  default     = 10
}

variable "ebs_volume_size" {
  description = "Optionally use EBS volumes for data storage by specifying volume size in GB (default 0)"
  default     = 10
}

variable "ebs_volume_type" {
  description = "Storage type of EBS volumes, if used (default gp2)"
  default     = "gp2"
}
