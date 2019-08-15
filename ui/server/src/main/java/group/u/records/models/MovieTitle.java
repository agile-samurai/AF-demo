package group.u.records.models;

import group.u.records.models.data.Movie;

import java.util.UUID;

public class MovieTitle {
    private String name;
    private String image;
    private UUID id;

    public MovieTitle(){}

    public MovieTitle(String name, String image, UUID id) {
        this.name = name;
        this.image = image;
        this.id = id;
    }

    public static MovieTitle from(Movie movie) {
        return new MovieTitle(movie.getName(), movie.getImage(), movie.getId());
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public UUID getId() {
        return id;
    }
}
