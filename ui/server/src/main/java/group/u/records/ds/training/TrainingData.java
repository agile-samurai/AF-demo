package group.u.records.ds.training;

import group.u.records.service.dossier.Lineage;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.Id;
import java.util.UUID;

@Document(indexName = "trainingdata", type = "trainingdata", shards = 1, replicas = 0, refreshInterval = "-1")
public class TrainingData {

    @Id
    private String id;
    private String key;

    public TrainingData(){}

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public TrainingData(String id) {
        this.id = UUID.nameUUIDFromBytes(id.getBytes()).toString();
        this.key = id;
    }
}
