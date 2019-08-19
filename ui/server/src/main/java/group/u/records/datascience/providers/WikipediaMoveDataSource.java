package group.u.records.datascience.providers;

import group.u.records.models.MovieDetail;
import group.u.records.service.dossier.Lineage;
import group.u.records.service.dossier.MovieDetailsDataSource;
import group.u.records.service.MovieIdentifier;
import group.u.records.service.datamanagement.S3DataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class WikipediaMoveDataSource extends MovieDetailsDataSource {
    private String folder;
    private S3DataService dataService;
    private Logger logger = LoggerFactory.getLogger(WikipediaMoveDataSource.class);

    public WikipediaMoveDataSource() {
        super(Lineage.WIKIPEDIA);
    }

    public WikipediaMoveDataSource(@Value("${aws.folder.wikipedia}") String folder,
                                   S3DataService dataService) {
        super(Lineage.WIKIPEDIA);
        this.folder = folder;
        this.dataService = dataService;
    }

    @Override
    public MovieDetail getMovieDetails(MovieIdentifier movieIdentifier) {
        try {
            return new MovieDetail(movieIdentifier.getId(), movieIdentifier.getName(), dataService.getFileAsString(folder + "/" + movieIdentifier.getImdbId() + ".wiki"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
