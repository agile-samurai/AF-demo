package group.u.records.datascience;

import group.u.records.datascience.providers.GenreDistributionClient;
import group.u.records.datascience.providers.GenreDistributionImageProvider;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class GenreDistributionImageProviderTest {

    @Test
    public void shouldGenerateGenreDistributionsUsingDataScienceService(){
        GenreDistributionClient client = mock(GenreDistributionClient.class);
        GenreDistributionImageProvider provider = new GenreDistributionImageProvider(client);

        provider.getJson("3481000");

    }
}
