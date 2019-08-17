package group.u.records.repository;

import group.u.records.models.entity.MoviePublicSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MoviePublicSummaryRepository extends ElasticsearchRepository<MoviePublicSummary, UUID> {
    @Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"name\"], \"fuzziness\": \"1\"}}")
    Page<MoviePublicSummary> findByMovieName(String term, Pageable pageable);

    Page<MoviePublicSummary> findAll(Pageable pageable);
}
