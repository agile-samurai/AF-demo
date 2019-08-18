package group.u.records.models.entity;

import group.u.records.models.Person;
import group.u.records.models.data.Movie;
import group.u.records.service.Lineage;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;

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

    public String getImage() {
        return image;
    }

    public String getGenre() {
        return genre;
    }
    private String image;
    private List<Review> reviews;

    public Lineage getLineage() {
        return lineage;
    }

    private Lineage lineage;
    private List<Person> people;
    private String summary;
    private List<String> keywords;
    private String contentRating;

    public MovieDetail(Movie movie, Lineage lineage){
        this(movie.getId(), movie.getName(), movie.getActor(), movie.getDescription(), null,
                 LocalDate.now(), movie.getGenre().get(0), movie.getImage(),asList(), lineage);
    }

    public UUID getId() {
        return id;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public MovieDetail(UUID id, String name, List<Person> people, String summary,
                       List<String> keywords, LocalDate releaseDate, String genre, String image, List<Review> reviews, Lineage lineage) {
        this.id = id;
        this.name = name;
        this.people = people;
        this.summary = summary;
        this.keywords = keywords;
        this.contentRating = contentRating;
        this.releaseDate = releaseDate;
        this.genre = genre;
        this.image = image;
        this.reviews = reviews;
        this.lineage = lineage;
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
