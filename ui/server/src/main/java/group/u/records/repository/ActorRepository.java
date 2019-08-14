package group.u.records.repository;

import group.u.records.models.Actor;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ActorRepository extends ElasticsearchRepository<Actor, UUID> {
}
