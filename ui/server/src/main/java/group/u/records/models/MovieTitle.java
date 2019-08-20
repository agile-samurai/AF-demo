package group.u.records.models;

import group.u.records.datasource.entity.Movie;
import group.u.records.models.entity.MoviePublicSummary;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Objects;
import java.util.UUID;

@Document(indexName = "movieTitle", type = "movieTitle", shards = 1, replicas = 0, refreshInterval = "-1")
public class MovieTitle {
    private String name;

    private boolean dossierAvailable;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieTitle that = (MovieTitle) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static MovieTitle from(MovieDetail movie) {
        return new MovieTitle(movie.getName(), movie.getImage(), movie.getId());
    }

    public static MovieTitle from(MoviePublicSummary movie) {
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

    public boolean isDossierAvailable() {
        return dossierAvailable;
    }

    public void setDossierAvailable(boolean dossierAvailable) {
        this.dossierAvailable = dossierAvailable;
    }
}
