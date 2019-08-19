package group.u.records.datascience;

import group.u.records.datascience.providers.AutoRedactClient;
import group.u.records.datascience.providers.PredictiveAutoRedactProvider;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class PredictiveAutoRedactProviderTest {

    private AutoRedactClient client = mock(AutoRedactClient.class);

    @Test
    public void shouldPostToDataScienceClassificationService(){
        PredictiveAutoRedactProvider provider = new PredictiveAutoRedactProvider(client);
    }
}
