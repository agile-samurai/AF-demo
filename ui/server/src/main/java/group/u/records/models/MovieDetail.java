package group.u.records.models;

import group.u.records.models.data.Movie;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class MovieDetail {
    public String getSummary() {
        return summary;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public String getContentRating() {
        return contentRating;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    @Override
    public String toString() {
        return "MovieDetails{" +
                "name='" + name + '\'' +
                ", actors=" + actors +
                ", summary='" + summary + '\'' +
                ", keywords=" + keywords +
                ", contentRating='" + contentRating + '\'' +
                ", releaseDate=" + releaseDate +
                '}';
    }

    private String name;

    public String getGenre() {
        return genre;
    }

    private List<Actor> actors;
    private String summary;
    private List<String> keywords;
    private String contentRating;

    public MovieDetail(Movie movie ){
        this(UUID.randomUUID(), movie.getName(), movie.getActor(), movie.getDescription(), null, movie.getContentRating(), LocalDate.now(), movie.getGenre().get(0));
    }

    public MovieDetail(UUID id, String name, List<Actor> actors, String summary,
                       List<String> keywords, String contentRating, LocalDate releaseDate, String genre) {
        this.name = name;
        this.actors = actors;
        this.summary = summary;
        this.keywords = keywords;
        this.contentRating = contentRating;
        this.releaseDate = releaseDate;
        this.genre = genre;
    }

    private LocalDate releaseDate;
    private String genre;


    public String getName() {
        return name;
    }

    public List<Actor> getActors() {
        return actors;
    }
}
