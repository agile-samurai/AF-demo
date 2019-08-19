package group.u.records.models.entity;

import group.u.records.models.Person;
import group.u.records.models.data.Movie;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import javax.persistence.Id;
import java.util.List;
import java.util.UUID;

import static org.springframework.data.elasticsearch.annotations.FieldType.Text;

@Document(indexName = "moviepublicsummary", type = "moviepublicsummary", shards = 1, replicas = 0, refreshInterval = "-1")
public class MoviePublicSummary {
    private boolean dossierAvailable;
    @Id
    private UUID id;

    private List<Person> people;

    @Field(type = Text, fielddata = true)
    private String name;

    public UUID getId() {
        return id;
    }

    public List<Person> getPeople() {
        return people;
    }

    public String getName() {
        return name;
    }

    public MoviePublicSummary(){}

    public MoviePublicSummary(Movie movie) {
        this.id = movie.getId();
        this.people = movie.getActor();
        this.name = movie.getName();
    }

    public MoviePublicSummary(MovieDetail movie) {
        this.id = movie.getId();
        this.people = movie.getPeople();
        this.name = movie.getName();
        this.dossierAvailable = true;
    }
}
