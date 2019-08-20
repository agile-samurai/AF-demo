package group.u.records.models;

import group.u.records.datasource.entity.OMDBMovie;
import group.u.records.datasource.entity.Movie;
import group.u.records.models.entity.Person;
import group.u.records.models.users.Review;
import group.u.records.service.dossier.Lineage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MovieDetail {

    private List<MovieCharacter> characters;

    public MovieDetail(UUID id, OMDBMovie movie, Lineage lineage) {
        this.id = id;
        this.name = movie.getTitle();
        this.summary = movie.getPlot();
        this.people = getActors(movie);
        this.lineage = lineage;
    }

    public MovieDetail(UUID id, String name, String description) {
        this.id = id;
        this.name = name;
        this.summary = description;
    }

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
    private List<Review> reviews = new ArrayList<>();

    public Lineage getLineage() {
        return lineage;
    }

    private Lineage lineage;
    private List<Person> people = new ArrayList<>();
    private String summary;
    private List<String> keywords;
    private String contentRating;

    public MovieDetail(Movie movie, List<MovieCharacter> characters, Lineage lineage){
        this(movie.getId(), movie.getName(), movie.getActor(), characters, movie.getDescription(), null,
                 LocalDate.now(), movie.getGenre().get(0), movie.getImage(),new ArrayList<>(), lineage);
    }

    public UUID getId() {
        return id;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public List<MovieCharacter> getCharacters() {
        return characters;
    }

    private List<Person> getActors(OMDBMovie movie) {
        String characterString = movie.getActors() == null ? "" : movie.getActors();
        return Arrays.stream(characterString.split(",")).map(Person::new).collect(Collectors.toList());
    }

    public MovieDetail(UUID id, String name, List<Person> people, List<MovieCharacter> characters, String summary,
                       List<String> keywords, LocalDate releaseDate, String genre, String image, List<Review> reviews, Lineage lineage) {
        this.id = id;
        this.name = name;
        this.people = people;
        this.characters = characters;
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
