package group.u.records.ds.providers;

import group.u.records.models.entity.MovieDetail;
import group.u.records.service.MovieDetailsDataSource;
import group.u.records.service.S3DataService;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

public class WikipediaMoveDataSource implements MovieDetailsDataSource {
    private String folder;
    private S3DataService dataService;

    public WikipediaMoveDataSource(@Value("${aws.folder.wikipedia}") String folder,
                                   S3DataService dataService ) {
        this.folder = folder;
        this.dataService = dataService;
    }

    @Override
    public MovieDetail getMovieDetails(String id) {
        try {
            dataService.getFileAsString(folder + "/" + id + ".wiki");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
