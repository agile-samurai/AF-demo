package group.u.mdas.repository;

import group.u.mdas.models.entity.TextClassificationElasticsearch;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface TextClassificationRepositoryElasticsearch extends ElasticsearchRepository<TextClassificationElasticsearch, String> {}
