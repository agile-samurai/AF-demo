package group.u.records.models;

import group.u.records.models.data.Movie;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import javax.persistence.Id;
import java.util.List;
import java.util.UUID;

import static org.springframework.data.elasticsearch.annotations.FieldType.Text;

@Document(indexName = "moviepublicsummary", type = "moviepublicsummary", shards = 1, replicas = 0, refreshInterval = "-1")
public class MoviePublicSummary {
    @Id
    private UUID id;

    private List<Actor> actors;

    @Field(type = Text, fielddata = true)
    private String name;

    public UUID getId() {
        return id;
    }

    public List<Actor> getActors() {
        return actors;
    }

    public String getName() {
        return name;
    }

    public MoviePublicSummary(){}

    public MoviePublicSummary(Movie movie) {
        this.id = movie.getId();
        this.actors = movie.getActor();
        this.name = movie.getName();
    }
}
