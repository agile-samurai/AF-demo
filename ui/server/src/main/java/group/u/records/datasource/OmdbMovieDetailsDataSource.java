package group.u.records.datasource;

import group.u.records.models.entity.MovieDetail;
import group.u.records.service.Lineage;
import group.u.records.service.MovieDetailsDataSource;
import group.u.records.service.S3DataService;
import org.springframework.stereotype.Component;

import static group.u.records.service.Lineage.OMDB;

@Component
public class OmdbMovieDetailsDataSource extends MovieDetailsDataSource {
    private S3DataService dataService;

    public OmdbMovieDetailsDataSource(S3DataService dataService) {
        super(OMDB);
        this.dataService = dataService;
    }

    @Override
    public MovieDetail getMovieDetails(String id) {
        return null;
    }
}
