package group.u.records.models.entity;

import group.u.records.models.Person;
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
                ", actors=" + people +
                ", summary='" + summary + '\'' +
                ", keywords=" + keywords +
                ", contentRating='" + contentRating + '\'' +
                ", releaseDate=" + releaseDate +
                '}';
    }

    private UUID id;
    private String name;

    public String getGenre() {
        return genre;
    }

    private List<Person> people;
    private String summary;
    private List<String> keywords;
    private String contentRating;

    public MovieDetail(Movie movie ){
        this(movie.getId(), movie.getName(), movie.getActor(), movie.getDescription(), null, movie.getContentRating(), LocalDate.now(), movie.getGenre().get(0));
    }

    public UUID getId() {
        return id;
    }

    public MovieDetail(UUID id, String name, List<Person> people, String summary,
                       List<String> keywords, String contentRating, LocalDate releaseDate, String genre) {
        this.id = id;
        this.name = name;
        this.people = people;
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

    public List<Person> getPeople() {
        return people;
    }
}
