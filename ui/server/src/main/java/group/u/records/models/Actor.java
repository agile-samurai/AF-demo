package group.u.records.models;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.data.elasticsearch.annotations.FieldType.Keyword;
import static org.springframework.data.elasticsearch.annotations.FieldType.Text;

@Document(indexName = "actor", type = "actor", shards = 1, replicas = 0, refreshInterval = "-1")
public class Actor {

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
    private List<MovieTitle> titles;

    public List<String> getAliases() {
        return aliases;
    }

    public List<MovieTitle> getTitles() {
        return titles;
    }

    public Actor(){
        titles = new ArrayList();
        aliases = new ArrayList();
    }

    public Actor(String name) {
        this.name = name;
        this.id = UUID.nameUUIDFromBytes(name.getBytes());
        titles = new ArrayList();
        aliases = new ArrayList();
        aliases.add(name);
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
        aliases.add(this.name);
        
        return id;
    }

    public String getUrl() {
        return url;
    }

    public void addTitle(MovieTitle movieTitle) {
        titles.add(movieTitle);
    }
}
