package group.u.records.models.entity;

import group.u.records.ds.providers.MovieSimilarityDataPoint;
import group.u.records.models.data.Movie;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.util.UUID;

import static org.springframework.data.elasticsearch.annotations.FieldType.Text;

@Document(indexName = "movieTitle", type = "movieTitle", shards = 1, replicas = 0, refreshInterval = "-1")
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

    public static MovieTitle from(MovieDetail movie) {
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

//    public static Movie from(MovieSimilarityDataPoint dataPoint) {
//        return new Movie(dataPoint.getName(), "fake", dataPoint.getId());
//    }
}
