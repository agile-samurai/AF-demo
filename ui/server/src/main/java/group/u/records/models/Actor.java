package group.u.records.models;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

import javax.persistence.Id;
import java.util.UUID;

import static org.springframework.data.elasticsearch.annotations.FieldType.Keyword;
import static org.springframework.data.elasticsearch.annotations.FieldType.Text;

@Document(indexName = "actor", type = "actor", shards = 1, replicas = 0, refreshInterval = "-1")
public class Actor {

    @Id
    private UUID id;
    @MultiField(
            mainField = @Field(type = Text, fielddata = true),
            otherFields = {
                    @InnerField(suffix = "verbatim", type = Keyword)
            }
    )
    private String fullName;

    public Actor(){}

    public Actor(String fullName) {
        this.fullName = fullName;
        this.id = UUID.nameUUIDFromBytes(fullName.getBytes());
    }

    public UUID getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    @Override
    public String toString() {
        return "Actor{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                '}';
    }
}
