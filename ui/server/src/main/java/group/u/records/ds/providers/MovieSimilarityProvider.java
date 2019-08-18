package group.u.records.ds.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import group.u.records.models.entity.MoviePublicSummary;
import group.u.records.models.entity.MovieTitle;
import group.u.records.repository.MoviePublicSummaryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.averagingDouble;
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

        ResponseEntity<String> rawResponse = restTemplate.getForEntity(host + "/most_similar/" + imdbId, String.class);
        try {
            logger.debug("Raw result:  " + rawResponse.getBody());
            Set<String> similar = new HashSet();
            Matcher m = Pattern.compile("tt\\d\\d*").matcher(rawResponse.getBody());
            while(m.find()){
                similar.add(m.group().substring(2));
            }


            logger.debug( "similar movies:  " + similar );
            return similar.stream().map(
                    s-> UUID.nameUUIDFromBytes(s.getBytes())).collect(toList());
//            return Arrays.stream(objectMapper.readValue(rawResponse.getBody(), HashMap.class))
//                    .map(MovieTitle::from)
//                    .collect(toList());


//            Arrays.stream(objectMapper.readValue(rawResponse.getBody()
//                    .replace("NaN", "\"NaN\""), HashMap[].class)).forEach(
//                    h -> {
//                        HashMap<String, Map> returnMap = new HashMap();
//                        for (Object key : h.keySet()) {
//                            logger.debug("key:  " + key);
//                            logger.debug( "value:  " + h.get(key));
//                            try {
//                                HashMap<String, String> map = objectMapper.readValue(h.get(key).toString(), HashMap.class);
//                                logger.debug( "new map:  " + map );
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
////                                return returnMap;
//                    }
//            );

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList();
    }

}
