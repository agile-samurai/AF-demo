package group.u.records.models;

import java.time.LocalDate;
import java.util.List;

public class MovieDetails {
    private final String name;
    private final List<Actor> actors;
    private String summary;
    private List<String> keywords;
    private String contentRating;

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
