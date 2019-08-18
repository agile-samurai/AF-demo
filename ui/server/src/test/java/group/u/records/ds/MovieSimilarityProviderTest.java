package group.u.records.ds;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import group.u.records.ds.providers.MovieSimilarityProvider;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

public class MovieSimilarityProviderTest {

    @Test
    @Ignore
    public void shouldFindAllMoviesThatAreSimilarToAGivenMovie() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false );
        MovieSimilarityProvider similarity = new MovieSimilarityProvider(new RestTemplate(), objectMapper, "http://dev-data-328021276.us-east-1.elb.amazonaws.com");

        try {
            List<UUID> movieTitles = similarity.getSimilarMovies("0337926");
            System.out.println(movieTitles.size());
        }catch( Exception e ){
            e.printStackTrace();
        }
    }
}
