package group.u.records.models;

import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.Id;
import java.util.UUID;

@Document(indexName = "actor", type = "actor", shards = 1, replicas = 0, refreshInterval = "-1")
public class Actor {

    @Id
    private UUID id;

    public UUID getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    private String fullName;
    public Actor(){}
    public Actor(String fullName) {
        this.fullName = fullName;
        this.id = UUID.nameUUIDFromBytes(fullName.getBytes());
    }
}
