package group.u.records.ds;

import group.u.records.ds.providers.GenreDistributionClient;
import group.u.records.ds.providers.GenreDistributionImageProvider;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class GenreDistributionImageProviderTest {

    @Test
    public void shouldGenerateGenreDistributionsUsingDataScienceService(){
        GenreDistributionClient client = mock(GenreDistributionClient.class);
        GenreDistributionImageProvider provider = new GenreDistributionImageProvider(client);

    }
}