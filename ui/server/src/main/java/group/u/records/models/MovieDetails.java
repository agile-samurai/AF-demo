package group.u.records.models;

import group.u.records.models.data.Movie;

import java.time.LocalDate;
import java.util.List;

public class MovieDetails {
    private String name;
    private List<Actor> actors;
    private String summary;
    private List<String> keywords;
    private String contentRating;

    public MovieDetails(Movie movie ){
        this(movie.getName(), movie.getActor(), movie.getDescription(), null, movie.getContentRating(), LocalDate.now());
    }
    public MovieDetails(String name, List<Actor> actors, String summary,
                        List<String> keywords, String contentRating, LocalDate releaseDate) {
        this.name = name;
        this.actors = actors;
        this.summary = summary;
        this.keywords = keywords;
        this.contentRating = contentRating;
        this.releaseDate = releaseDate;
    }

    private LocalDate releaseDate;


    public String getName() {
        return name;
    }

    public List<Actor> getActors() {
        return actors;
    }
}
