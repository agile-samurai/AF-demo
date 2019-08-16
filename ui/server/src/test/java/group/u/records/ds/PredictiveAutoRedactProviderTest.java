package group.u.records.ds;

import org.junit.Test;

import static org.mockito.Mockito.mock;

public class PredictiveAutoRedactProviderTest {

    private AutoRedactClient client = mock(AutoRedactClient.class);

    @Test
    public void shouldPostToDataScienceClassificationService(){
        PredictiveAutoRedactProvider provider = new PredictiveAutoRedactProvider(client);
    }
}
