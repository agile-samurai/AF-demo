package group.u.records.datasource;

import com.fasterxml.jackson.databind.ObjectMapper;
import group.u.records.models.data.Movie;
import group.u.records.models.entity.MovieDetail;
import group.u.records.service.MovieDetailsDataSource;
import group.u.records.service.S3DataService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ImdbMovieDetailsDataSource implements MovieDetailsDataSource {

    private String bucketName;
    private String folder;
    private ObjectMapper objectMapper;
    private S3DataService dataService;

    public ImdbMovieDetailsDataSource(@Value("${aws.bucketName}") String bucketName,
                                      @Value("${aws.folder}") String folder,
                                      ObjectMapper objectMapper,
                                      S3DataService dataService) {
        this.bucketName = bucketName;
        this.folder = folder;
        this.objectMapper = objectMapper;
        this.dataService = dataService;
    }

    @Override
    public MovieDetail getMovieDetails(String id) {
        MovieDetail movieDetail = null;

        try {
            String json = dataService.getFileAsString(this.convertId(folder, id ), bucketName);
            Movie movie = objectMapper.readValue(json, Movie.class);
            movie.enrichModel();
            movieDetail = new MovieDetail(movie);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return movieDetail;
    }

    private String convertId(String bucketName, String imdb) {
        return bucketName + "/tt" + imdb + ".json";
    }
}
