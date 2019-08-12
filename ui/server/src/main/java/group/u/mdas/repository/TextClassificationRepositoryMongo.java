package group.u.mdas.repository;

import group.u.mdas.models.entity.TextClassificationMongo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TextClassificationRepositoryMongo extends MongoRepository<TextClassificationMongo, String> {}
