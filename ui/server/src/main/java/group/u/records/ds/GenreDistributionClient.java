package group.u.records.ds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
public class GenreDistributionClient {
    private RestTemplate restTemplate;
    private Logger logger = LoggerFactory.getLogger(GenreDistributionClient.class);
    private String host;

    public GenreDistributionClient(RestTemplate restTemplate,
                                   @Value("$app.ds.images.host}") String host ){
        this.restTemplate = restTemplate;
        this.host = host;
    }

    public String getImageStructure(UUID dossierId){
        try {
            return restTemplate.postForEntity(host + "/make_test_plot",
                    new HttpEntity(""), String.class).getBody();
        }catch( Exception e ){
            logger.debug("Image Classification is unavailable" );
        }

        return "";
    }
}
