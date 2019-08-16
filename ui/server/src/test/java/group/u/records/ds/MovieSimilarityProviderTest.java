package group.u.records.ds;

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
