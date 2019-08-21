package group.u.records.datasource;

import com.fasterxml.jackson.databind.ObjectMapper;
import group.u.records.datasource.entity.Movie;
import group.u.records.models.MovieCharacter;
import group.u.records.models.MovieDetail;
import group.u.records.repository.people.PersonRegistry;
import group.u.records.service.dossier.MovieDetailsDataSource;
import group.u.records.service.MovieIdentifier;
import group.u.records.service.datamanagement.S3DataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static group.u.records.service.dossier.models.Lineage.IMDB;
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
    public MovieDetail getMovieDetails(MovieIdentifier id) {
        MovieDetail movieDetail = null;

        try {
            String json = dataService.getFileAsString(this.convertId(folder, id.getImdbId() ));
            Movie movie = objectMapper.readValue(json, Movie.class);
            movie.enrichModel(id.getImdbId());
            final List<MovieCharacter> characters = getCharacters(id.getImdbId());
            logger.debug("Extracting Characters:  " + characters );

            movieDetail = new MovieDetail(movie, characters, this.getLineage());
            movieDetail.getPeople().forEach(p->personRegistry.reconcile(p, new MovieDetail(movie, characters, this.getLineage())));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return movieDetail;
    }

    public List<MovieCharacter> getCharacters(String id ){
        try {
            String json = dataService.getFileAsString(this.convertId(characterFolder, id ));
            logger.debug("Character information being processed:  " + json );
            return asList(objectMapper.readValue(json,MovieCharacter[].class));
        } catch (IOException e) {
            logger.error("Error while extracting characters for movie:  " + id );
        }

        return new ArrayList<>();
    }

    private String convertId(String folderName, String imdb) {
        String key = folderName + "/tt" + imdb + ".json";
        logger.debug("Expected folder key:  " + key);
        return key;
    }
}
