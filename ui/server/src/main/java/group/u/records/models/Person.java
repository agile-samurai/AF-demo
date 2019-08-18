package group.u.records.models;

import group.u.records.models.entity.MovieTitle;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.data.elasticsearch.annotations.FieldType.Keyword;
import static org.springframework.data.elasticsearch.annotations.FieldType.Nested;
import static org.springframework.data.elasticsearch.annotations.FieldType.Text;

@Document(indexName = "actor", type = "actor", shards = 1, replicas = 0, refreshInterval = "-1")
public class Person {

    private List<String> aliases;
    @Id
    private UUID id;
    private String url;
    @MultiField(
            mainField = @Field(type = Text, fielddata = true),
            otherFields = {
                    @InnerField(suffix = "verbatim", type = Keyword)
            }
    )
    private String name;

    @Field(type = Nested)
    private List<MovieTitle> titles;

    public List<String> getAliases() {
        return aliases;
    }

    public List<MovieTitle> getTitles() {
        return titles;
    }

    public Person(){
        titles = new ArrayList<>();
        aliases = new ArrayList<>();
    }

    public Person(String name, List<String> aliases, List<MovieTitle> titles) {
        this.name = name;
        this.id = UUID.nameUUIDFromBytes(name.getBytes());
        this.titles = titles;
        this.aliases = aliases;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Actor{" +
                "id=" + id +
                ", fullName='" + name + '\'' +
                '}';
    }

    public UUID enrichModel() {
        id = UUID.nameUUIDFromBytes(url.getBytes());

        return id;
    }

    public String getUrl() {
        return url;
    }

    public void addTitle(MovieTitle movieTitle) {
        titles.add(movieTitle);
    }

    public void addTitles(List<MovieTitle> movieTitles) {
        movieTitles.forEach(this::addTitle);
    }
}
