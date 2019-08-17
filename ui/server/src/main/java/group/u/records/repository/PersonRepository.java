package group.u.records.repository;

import group.u.records.models.Person;
import group.u.records.models.entity.MoviePublicSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PersonRepository extends ElasticsearchRepository<Person, UUID> {
    @Query("{\"bool\": {\"should\": [{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"aliases\"], \"fuzziness\": \"1\"}}, {\"nested\": {\"path\": \"titles\", \"query\": {\"match\": {\"titles.name\": {\"query\": \"?0\", \"fuzziness\": \"1\"}}}}}]}}")
    Page<Person> findByActorNameOrActorAliasOrTitleName(String term, Pageable pageable);
}
