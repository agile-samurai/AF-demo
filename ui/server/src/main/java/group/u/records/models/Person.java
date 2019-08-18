package group.u.records.models;

import group.u.records.models.entity.MovieTitle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.*;

import static org.springframework.data.elasticsearch.annotations.FieldType.*;

@Document(indexName = "actor", type = "actor", shards = 1, replicas = 0, refreshInterval = "-1")
public class Person {

    @Transient
    private Logger logger = LoggerFactory.getLogger(Person.class);
    private Set<String> aliases;
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

    public Set<String> getAliases() {
        return aliases;
    }

    public List<MovieTitle> getTitles() {
        return titles;
    }

    public Person(){
        titles = new ArrayList<>();
        aliases = new HashSet<>();
    }

    public Person(String url, String name, Set<String> aliases, List<MovieTitle> titles) {
        this.url = url;
        this.name = name;
        this.id = UUID.nameUUIDFromBytes(url.getBytes());
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

    public void mergeIfPossible(Person personFromCharacter) {

        if(personFromCharacter.getId() == this.getId()){
            logger.debug("character aliases  " + personFromCharacter.getAliases());
            aliases.addAll(personFromCharacter.getAliases());
            logger.debug("Merging characters:  " + aliases);
        }
    }
}
