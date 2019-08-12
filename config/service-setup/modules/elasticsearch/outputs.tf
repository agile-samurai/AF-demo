output "ElasticSearchEndpoint" {
  value = "${aws_elasticsearch_domain.es.endpoint}"
}

output "KibanaEndpoint" {
  value = "${aws_elasticsearch_domain.es.kibana_endpoint}"
}

output "es_security_group_id" {
  value = "${aws_security_group.elasticsearch.id}"
}
