package group.u.records.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import group.u.records.models.MovieTweet;
import group.u.records.models.entity.MovieDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TwitterMovieDataSource extends MovieDetailsDataSource{
    private Map<String, List<String>> tweetMap;
    private DataService dataService;
    private Logger logger = LoggerFactory.getLogger(TwitterMovieDataSource.class);

    public TwitterMovieDataSource(S3DataService dataService, ObjectMapper objectMapper) {
        super(Lineage.TWITTER);
        this.dataService = dataService;

        String file = dataService.getFile( "rdso-challenge2", "data/tweets/parsed/tweet_data.json" );
        try {
            Arrays.stream(objectMapper.readValue(file, MovieTweet[].class)).forEach(t->{
                tweetMap.put(t.getImdb_id(), t.getTweet_id());
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.debug("File:  " + file );
    }


    @Override
    public MovieDetail getMovieDetails(String id) {

        return null;
//        return new MovieDetail();
    }
}
