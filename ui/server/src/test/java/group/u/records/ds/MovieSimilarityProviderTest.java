package group.u.records.ds;

import group.u.records.ds.providers.MovieSimilarityProvider;
import group.u.records.ds.providers.SimilarityClient;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class MovieSimilarityProviderTest {

    @Test
    public void shouldFindAllMoviesThatAreSimilarToAGivenMovie() {
        SimilarityClient client = mock(SimilarityClient.class);
        MovieSimilarityProvider similarityProvider =
                new MovieSimilarityProvider(client);
    }
}
