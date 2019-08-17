package group.u.records.service;

import group.u.records.models.data.Movie;
import group.u.records.models.entity.MovieDetail;
import org.springframework.stereotype.Component;

@Component
public class AmazonReviewsDataSource implements MovieDetailsDataSource {

    private S3DataService dataService;
    public enum DataSource {
        IMDB,
        AMAZON,
        OMDB,
        CSV
    }

    public AmazonReviewsDataSource(S3DataService dataService) {
        this.dataService = dataService;
    }

    @Override
    public MovieDetail getMovieDetails(String id) {
        return new MovieDetail(new Movie());
//        return dataService.processMovie(id);
    }
}
