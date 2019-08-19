package group.u.records.models;

import java.util.List;

public class MovieTweet {
    String imdb_id;
    List<String> tweet_id;

    public String getImdb_id() {
        return imdb_id;
    }

    public List<String> getTweet_id() {
        return tweet_id;
    }
}
