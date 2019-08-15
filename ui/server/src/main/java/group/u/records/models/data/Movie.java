package group.u.records.models.data;

import group.u.records.models.Actor;

import java.util.List;

public class Movie {
    private String name;
    private String image;

    public Movie() {
    }

    public Movie(String name, String image, String contentRating, List<String> genre, String description, List<Actor> actor) {
        this.name = name;
        this.image = image;
        this.contentRating = contentRating;
        this.genre = genre;
        this.description = description;
        this.actor = actor;
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
}
