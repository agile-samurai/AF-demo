package group.u.records.models.data;

import group.u.records.models.Actor;

import java.util.List;
import java.util.UUID;

public class Movie {
    private String name;
    private String image;
    private String url;
    private UUID id;

    public Movie() {
    }

    public Movie(String name, String image, String contentRating, List<String> genre, String description, List<Actor> actor, String url) {
        this.name = name;
        this.image = image;
        this.contentRating = contentRating;
        this.genre = genre;
        this.description = description;
        this.actor = actor;
        this.url = url;
        enrichModel();
    }

    private String contentRating;
    private List<String> genre;
    private String description;
    private List<Actor> actor;

    public List<String> getGenre() {
        return genre;
    }

    public String getDescription() {
        return description;
    }

    public List<Actor> getActor() {
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

    public void enrichModel() {
        this.id = UUID.nameUUIDFromBytes(url.getBytes());
    }
}
