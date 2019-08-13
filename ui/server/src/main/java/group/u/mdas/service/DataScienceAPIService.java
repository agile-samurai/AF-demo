package group.u.mdas.service;

import group.u.mdas.models.web.DataScienceHealthCheckResponse;
import group.u.mdas.models.web.TextClassificationResponse;
import org.awaitility.Awaitility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class DataScienceAPIService {
    private static final String HEALTH_CHECK_SUCCESS_MESSAGE = "Up and running";
    private RestTemplate restTemplate;
    private String dataScienceBaseURL;
    private Logger logger = LoggerFactory.getLogger(DataScienceAPIService.class);

    public DataScienceAPIService(RestTemplate restTemplate,
                                 @Value("${data.science.service.baseurl}") String dataScienceBaseURL) {
        this.restTemplate = restTemplate;
        this.dataScienceBaseURL = dataScienceBaseURL;
    }

    void waitForAPIReady() {
        Awaitility.await()
                .pollInterval(1, TimeUnit.SECONDS)
                .atMost(5, TimeUnit.MINUTES)
                .until(this::isAPIReady);
    }

    boolean isAPIReady() {
        try {
            DataScienceHealthCheckResponse dataScienceHealthCheckResponse = restTemplate.getForObject(dataScienceBaseURL + "/health/check",
                    DataScienceHealthCheckResponse.class);

            return Objects.nonNull(dataScienceHealthCheckResponse) &&
                    dataScienceHealthCheckResponse.getMessage().equals(HEALTH_CHECK_SUCCESS_MESSAGE);
        } catch (Exception exception) {
            logger.debug(exception.getMessage());
        }

        return false;
    }

    public String getHelloWorldScore() {
        TextClassificationResponse textClassificationResponse = new TextClassificationResponse();
        try {
            textClassificationResponse.setText("hello World");
            textClassificationResponse.setComparisonText("hello wrld");
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<TextClassificationResponse> httpEntity = new HttpEntity<>(textClassificationResponse, httpHeaders);
            ResponseEntity<String> helloWorldScore =
                    restTemplate.postForEntity(dataScienceBaseURL + "/similarity_score",
                            httpEntity,
                            String.class
                            );
            return helloWorldScore.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug("Failed to make call to python service. Check the service. Continuing");
        }
        return null;
    }

    public String getMetrics() {
        try {
            ResponseEntity<String> metrics = restTemplate.getForEntity(dataScienceBaseURL + "/metrics", String.class);

            return metrics.getBody();
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
