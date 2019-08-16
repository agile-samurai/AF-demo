package group.u.records.ds;

import org.junit.Test;

import static org.mockito.Mockito.mock;

public class GenreDistributionImageProviderTest {

    @Test
    public void shouldGenerateGenreDistributionsUsingDataScienceService(){
        GenreDistributionClient client = mock(GenreDistributionClient.class);
        GenreDistributionImageProvider provider = new GenreDistributionImageProvider(client);

    }
}
