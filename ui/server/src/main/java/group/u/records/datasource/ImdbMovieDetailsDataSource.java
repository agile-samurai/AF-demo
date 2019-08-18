package group.u.records.datasource;

import com.fasterxml.jackson.databind.ObjectMapper;
import group.u.records.models.data.Movie;
import group.u.records.models.entity.MovieDetail;
import group.u.records.people.PersonRegistry;
import group.u.records.service.MovieDetailsDataSource;
import group.u.records.service.S3DataService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static group.u.records.service.Lineage.IMDB;

@Component
public class ImdbMovieDetailsDataSource extends MovieDetailsDataSource {

    private String bucketName;
    private String folder;
    private PersonRegistry personRegistry;
    private ObjectMapper objectMapper;
    private S3DataService dataService;

    public ImdbMovieDetailsDataSource(@Value("${aws.bucketName}") String bucketName,
                                      @Value("${aws.folder}") String folder,
                                      PersonRegistry personRegistry,
                                      ObjectMapper objectMapper,
                                      S3DataService dataService) {
        super(IMDB);
        this.bucketName = bucketName;
        this.folder = folder;
        this.personRegistry = personRegistry;
        this.objectMapper = objectMapper;
        this.dataService = dataService;
    }

    @Override
    public MovieDetail getMovieDetails(String id) {
        MovieDetail movieDetail = null;

        try {
            String json = dataService.getFileAsString(this.convertId(folder, id ));
            Movie movie = objectMapper.readValue(json, Movie.class);
            movie.enrichModel();
            movieDetail = new MovieDetail(movie, this.getLineage());

            movieDetail.getPeople().forEach(p->personRegistry.reconcile(p, new MovieDetail(movie, this.getLineage())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return movieDetail;
    }

    private String convertId(String bucketName, String imdb) {
        return bucketName + "/tt" + imdb + ".json";
    }
}
