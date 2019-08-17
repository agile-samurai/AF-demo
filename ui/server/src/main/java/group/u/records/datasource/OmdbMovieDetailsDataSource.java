package group.u.records.datasource;

import group.u.records.models.entity.MovieDetail;
import group.u.records.service.S3DataService;
import org.springframework.stereotype.Component;

@Component
public class OmdbMovieDetailsDataSource implements group.u.records.service.MovieDetailsDataSource {
    private S3DataService dataService;

    public OmdbMovieDetailsDataSource(S3DataService dataService) {
        this.dataService = dataService;
    }

    @Override
    public MovieDetail getMovieDetails(String id) {
        return null;
    }
}
