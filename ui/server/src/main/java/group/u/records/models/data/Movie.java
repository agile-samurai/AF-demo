package group.u.records.models.data;

import group.u.records.models.Actor;

import java.util.List;

public class Movie {
    private String name;
    private String image;
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
