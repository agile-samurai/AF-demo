package group.u.records.ds;

import group.u.records.ds.providers.GenreDistributionClient;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


public class GenreDistributionClientTest {

    @Test
    public void shouldProvideJsonImagesFromTheGenreService() {
        GenreDistributionClient client = new GenreDistributionClient(new RestTemplate(),
                "http://localhost:8000", false );
        String text = client.getImageStructure(UUID.randomUUID());
        assertThat(text).isNotNull();

        System.out.println(text);
    }

}
