package group.u.records.datasource;

import com.fasterxml.jackson.databind.ObjectMapper;
import group.u.records.models.data.Movie;
import group.u.records.models.entity.MovieCharacter;
import group.u.records.models.entity.MovieDetail;
import group.u.records.people.PersonRegistry;
import group.u.records.service.MovieDetailsDataSource;
import group.u.records.service.S3DataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

import static group.u.records.service.Lineage.IMDB;
import static java.lang.Enum.valueOf;
import static java.util.Arrays.asList;

@Component
public class ImdbMovieDetailsDataSource extends MovieDetailsDataSource {
    private String folder;
    private String characterFolder;
    private PersonRegistry personRegistry;
    private ObjectMapper objectMapper;
    private S3DataService dataService;
    private Logger logger = LoggerFactory.getLogger(ImdbMovieDetailsDataSource.class);

    public ImdbMovieDetailsDataSource(@Value("${aws.folder}") String folder,
                                      @Value("${aws.characterFolder}") String characterFolder,
                                      PersonRegistry personRegistry,
                                      ObjectMapper objectMapper,
                                      S3DataService dataService) {
        super(IMDB);
        this.folder = folder;
        this.characterFolder = characterFolder;
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
            movie.enrichModel(id);
            movieDetail = new MovieDetail(movie, this.getLineage());
            movieDetail.getPeople().forEach(p->personRegistry.reconcile(p, new MovieDetail(movie, this.getLineage())));
            getCharacters(id);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return movieDetail;
    }

    public List<MovieCharacter> getCharacters(String id ){
        try {
            String json = dataService.getFileAsString(this.convertId(characterFolder, id ));
            logger.debug("Character information being processed:  " + json );
            asList(objectMapper.readValue(json,MovieCharacter[].class));
        } catch (IOException e) {
            logger.error("Error while extracting characters for movie:  " + id );
        }

        return null;
    }

    private String convertId(String folderName, String imdb) {
        return folderName + "/tt" + imdb + ".json";
    }
}
