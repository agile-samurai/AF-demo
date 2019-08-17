package group.u.records.repository;

import group.u.records.models.entity.Actor;
import group.u.records.models.entity.MoviePublicSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ActorRepository extends ElasticsearchRepository<Actor, UUID> {
    @Query("{\"bool\": {\"should\": [{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"aliases\"], \"fuzziness\": \"1\"}}, {\"nested\": {\"path\": \"titles\", \"query\": {\"match\": {\"titles.name\": {\"query\": \"?0\", \"fuzziness\": \"1\"}}}}}]}}")
    Page<Actor> findByActorNameOrActorAliasOrTitleName(String term, Pageable pageable);
}
