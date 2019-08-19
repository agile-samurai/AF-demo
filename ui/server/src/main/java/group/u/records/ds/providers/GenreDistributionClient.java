package group.u.records.ds.providers;

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
    private boolean enabledDistributionImages;

    public GenreDistributionClient(RestTemplate restTemplate,
                                   @Value("$app.ds.images.host}") String host,
                                   @Value("${app.feature.enableDistributionImages}") boolean enabledDistributionImages ){
        this.restTemplate = restTemplate;
        this.host = host;
        this.enabledDistributionImages = enabledDistributionImages;
    }

    public String getImageStructure(UUID dossierId){
        if( !enabledDistributionImages ) return "";

        try {
            return restTemplate.postForEntity(host + "/highlighted_film_plot/" + dossierId,
                    new HttpEntity(""), String.class).getBody();
        }catch( Exception e ){
            logger.debug("Image Classification is unavailable" );
        }

        return "";
    }
}
