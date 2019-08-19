package group.u.records.datascience;

import group.u.records.datascience.providers.GenreDistributionClient;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;


public class GenreDistributionClientTest {

    @Test
    public void shouldProvideJsonImagesFromTheGenreService() {
        GenreDistributionClient client = new GenreDistributionClient(new RestTemplate(),
                "http://localhost:8000", true );
        String text = client.getImageStructure("3481000");
        assertThat(text).isNotNull();

        System.out.println(text);
    }

}
