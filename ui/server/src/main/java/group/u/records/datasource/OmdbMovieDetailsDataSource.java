package group.u.records.datasource;

import com.fasterxml.jackson.databind.ObjectMapper;
import group.u.records.models.entity.MovieDetail;
import group.u.records.people.PersonRegistry;
import group.u.records.service.MovieDetailsDataSource;
import group.u.records.service.MovieIdentifier;
import group.u.records.service.S3DataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static group.u.records.service.Lineage.OMDB;

@Component
public class OmdbMovieDetailsDataSource extends MovieDetailsDataSource {
    private S3DataService dataService;
    private PersonRegistry personRegistry;
    private ObjectMapper objectMapper;
    private Logger logger = LoggerFactory.getLogger(OmdbMovieDetailsDataSource.class);

    public OmdbMovieDetailsDataSource(S3DataService dataService,
                                      PersonRegistry personRegistry,
                                      ObjectMapper objectMapper ) {
        super(OMDB);
        this.dataService = dataService;
        this.personRegistry = personRegistry;
        this.objectMapper = objectMapper;
    }

    @Override
    public MovieDetail getMovieDetails(MovieIdentifier id) {
        MovieDetail movieDetail = null;

        try {
            String json = dataService.getFileAsString("data/omdb_json/tt" + id.getImdbId() + ".json");
            json = json.replace("\"Title\"", "\"title\"");
            json = json.replace("\"Plot\"", "\"plot\"");
            json = json.replace("\"Actors\"", "\"actors\"");

            logger.debug("Data  " + json );
            OMDBMovie movie = objectMapper.readValue(json, OMDBMovie.class);
            return new MovieDetail(id.getId(), movie, getLineage());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return movieDetail;
    }

}
