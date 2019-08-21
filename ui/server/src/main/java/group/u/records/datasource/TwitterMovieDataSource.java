package group.u.records.datasource;

import com.fasterxml.jackson.databind.ObjectMapper;
import group.u.records.models.users.MovieTweet;
import group.u.records.service.*;
import group.u.records.service.datamanagement.DataService;
import group.u.records.service.datamanagement.S3DataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TwitterMovieDataSource{
    private Map<String, List<String>> tweetMap = new HashMap<>();
    private DataService dataService;
    private Logger logger = LoggerFactory.getLogger(TwitterMovieDataSource.class);

    public TwitterMovieDataSource(@Value("${aws.tweets}") String folder,
                                  @Value("${aws.bucketName}") String bucketName,
                                  S3DataService dataService,
                                  ObjectMapper objectMapper) {
        this.dataService = dataService;
        String file = dataService.getFile( bucketName, folder + "/tweet_data.json" );
        try {
            Arrays.stream(objectMapper.readValue(file, MovieTweet[].class)).forEach(t->{
                tweetMap.put(t.getImdb_id(), t.getTweet_id());
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.debug("File:  " + file );
    }


    public List<String> getMovieDetails(MovieIdentifier identifier) {
        return tweetMap.get(identifier.getImdbId());
    }
}
