package group.u.records.datascience.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

@Component
public class MovieSimilarityProvider {
    private final RestTemplate restTemplate;
    private ObjectMapper objectMapper;
    private final String host;
    private Logger logger = LoggerFactory.getLogger(MovieSimilarityProvider.class);

    public MovieSimilarityProvider(RestTemplate restTemplate,
                                   ObjectMapper objectMapper,
                                   @Value("${app.ds.similarities.host}") String host) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.host = host;
    }

    public List<UUID> getSimilarMovies(String imdbId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        try {
            ResponseEntity<String> rawResponse = restTemplate.getForEntity(host + "/most_similar/" + imdbId, String.class);
            logger.debug("Raw result:  " + rawResponse.getBody());
            Set<String> similar = new HashSet();
            Matcher m = Pattern.compile("tt\\d\\d*").matcher(rawResponse.getBody());
            while (m.find()) {
                similar.add(m.group().substring(2));
            }

            logger.debug("similar movies:  " + similar);
            return similar.stream().map(
                    s -> UUID.nameUUIDFromBytes(s.getBytes())).collect(toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList();
    }

}
