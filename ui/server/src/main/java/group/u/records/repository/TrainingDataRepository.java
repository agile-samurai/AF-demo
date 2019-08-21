package group.u.records.repository;

import group.u.records.ds.training.TrainingData;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingDataRepository extends ElasticsearchRepository<TrainingData, String> {
}
