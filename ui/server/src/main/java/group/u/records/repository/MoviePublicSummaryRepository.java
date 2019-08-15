package group.u.records.repository;

import group.u.records.models.MoviePublicSummary;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MoviePublicSummaryRepository extends ElasticsearchRepository<MoviePublicSummary, UUID> {
}
