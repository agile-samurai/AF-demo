package group.u.records.models.data;

import group.u.records.models.Person;

import java.util.List;
import java.util.UUID;

public class Movie {
    private String name;
    private String image;
    private String url;
    private UUID id;

    public Movie() {
    }

    public Movie(String name, String image, String contentRating, List<String> genre, String description, List<Person> actor, String url) {
        this.name = name;
        this.image = image;
        this.contentRating = contentRating;
        this.genre = genre;
        this.description = description;
        this.actor = actor;
        this.url = url;
    }

    private String contentRating;
    private List<String> genre;
    private String description;
    private List<Person> actor;
    private List<Person> director;
    private List<Person> creator;

    public List<Person> getDirector() {
        return director;
    }

    public List<Person> getCreator() {
        return creator;
    }

    public List<String> getGenre() {
        return genre;
    }

    public String getDescription() {
        return description;
    }

    public List<Person> getActor() {
        return actor;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getContentRating() {
        return contentRating;
    }

    public UUID getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public void enrichModel(String imdbId ) {
        getActor().forEach(Person::enrichModel);
        this.id = UUID.nameUUIDFromBytes(imdbId.getBytes());
    }
}
