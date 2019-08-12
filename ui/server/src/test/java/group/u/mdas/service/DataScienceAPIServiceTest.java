package group.u.mdas.service;

import group.u.mdas.models.entity.TextClassificationMongo;
import group.u.mdas.models.entity.TextClassificationElasticsearch;
import group.u.mdas.models.entity.TextClassificationPostgres;
import group.u.mdas.models.web.DataScienceHealthCheckResponse;
import group.u.mdas.repository.TextClassificationRepositoryElasticsearch;
import group.u.mdas.repository.TextClassificationRepositoryMongo;
import group.u.mdas.repository.TextClassificationRepositoryPostgres;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class DataScienceAPIServiceTest {

    private RestTemplate restTemplate;
    private DataScienceAPIService dataScienceAPIService;
    private TextClassificationRepositoryPostgres textClassificationRepositoryPostgres;

    @Before
    public void setUp() {
        restTemplate = mock(RestTemplate.class);
        textClassificationRepositoryPostgres =
                mock(TextClassificationRepositoryPostgres.class);
        TextClassificationRepositoryMongo textClassificationRepositoryMongo =
                mock(TextClassificationRepositoryMongo.class);
        TextClassificationRepositoryElasticsearch classificationRepositoryElasticsearchMock = mock(TextClassificationRepositoryElasticsearch.class);

        when(textClassificationRepositoryPostgres.save(any()))
                .thenReturn(new TextClassificationPostgres("text 2",
                        "comparison text 2",
                        "0.81"));
        when(textClassificationRepositoryPostgres.findById(any()))
                .thenReturn(Optional.of(new TextClassificationPostgres("text 1",
                        "comparison text 1",
                        "3.0")));

        when(textClassificationRepositoryMongo.save(any(TextClassificationMongo.class))).thenReturn(
                new TextClassificationMongo("text 1",
                "comparison text 1",
                "3.0"));
        when(classificationRepositoryElasticsearchMock.save(any()))
                .thenReturn(new TextClassificationElasticsearch("text 2",
                        "comparison text 2",
                        "0.81"));

        String url = "http://testUrl";
        dataScienceAPIService = new DataScienceAPIService(restTemplate,
                url,
                textClassificationRepositoryPostgres,
                textClassificationRepositoryMongo, classificationRepositoryElasticsearchMock);
    }

    @Test
    public void waitForAPIReady_waitsForApiToBeReady() {
        DataScienceHealthCheckResponse response = new DataScienceHealthCheckResponse();
        response.setMessage("Up and running");
        when(restTemplate.getForObject(anyString(), any())).thenReturn(response);

        // the test completes, which proves that this call is working
        dataScienceAPIService.waitForAPIReady();
    }

    @Test
    public void isAPIReady_properlyChecksIfApiIsReady() {
        DataScienceHealthCheckResponse response = new DataScienceHealthCheckResponse();
        response.setMessage("Up and running");
        when(restTemplate.getForObject(anyString(), any())).thenReturn(response);

        boolean apiReady = dataScienceAPIService.isAPIReady();

        assertTrue(apiReady);
    }

    @Test
    public void isAPIReady_returnsFalse_whenWrongMessageSent() {
        DataScienceHealthCheckResponse response = new DataScienceHealthCheckResponse();
        response.setMessage("hiccup");
        when(restTemplate.getForObject(anyString(), any())).thenReturn(response);

        boolean apiReady = dataScienceAPIService.isAPIReady();

        assertFalse(apiReady);
    }

    @Test
    public void isAPIReady_returnsFalse_whenDataScienceNull() {
        DataScienceHealthCheckResponse response = null;
        when(restTemplate.getForObject(anyString(), any())).thenReturn(response);

        boolean apiReady = dataScienceAPIService.isAPIReady();

        assertFalse(apiReady);
    }

    @Test
    public void isAPIReady_fails_returnsFalse() {
        DataScienceHealthCheckResponse response = new DataScienceHealthCheckResponse();
        response.setMessage("Up and running");
        when(restTemplate.getForObject(anyString(), any()))
                .thenAnswer((Answer<DataScienceHealthCheckResponse>) invocation -> {
                    throw new Exception();
                });

        boolean apiReady = true;
        try {
            apiReady = dataScienceAPIService.isAPIReady();
        } catch (Exception e) {
            assertFalse(apiReady);
        }
    }

    @Test
    public void getHelloWorldScore() {
        String start = "3.0";
        String expected = "3.03.00.81";
        when(restTemplate.postForEntity(anyString(),
                any(HttpEntity.class),
                ArgumentMatchers.<Class<String>>any()))
                .thenReturn(ResponseEntity.ok(start));

        String actual = dataScienceAPIService.getHelloWorldScore();

        assertEquals(expected, actual);
    }

    @Test
    public void getHelloWorldScore_shouldReturnNull_whenClassificationNotPresent() {
        String start = "3.0";
        when(restTemplate.postForEntity(anyString(),
                any(HttpEntity.class),
                ArgumentMatchers.<Class<String>>any()))
                .thenReturn(ResponseEntity.ok(start));
        when(textClassificationRepositoryPostgres.findById(any())).thenReturn(Optional.empty());

        String actual = dataScienceAPIService.getHelloWorldScore();

        assertNull(actual);
    }

    @Test
    public void getHelloWorldScore_shouldThrow() {
        when(restTemplate.postForEntity(anyString(),
                any(HttpEntity.class),
                ArgumentMatchers.<Class<String>>any()))
                .thenAnswer((Answer<String>) invocation -> {
                    throw new Exception();
                });

        String helloWorldScore = "shouldBeSetToNull";

        try {
            helloWorldScore = dataScienceAPIService.getHelloWorldScore();
        } catch (Exception e) {
            assertNull(helloWorldScore);
        }
    }

    @Test
    public void getMetrics() {
        String expected = "{'Model 1': 10, 'Model 2': 100, 'Model 3': 1000}";
        when(restTemplate.getForEntity(anyString(),
                ArgumentMatchers.<Class<String>>any()))
            .thenReturn(ResponseEntity.ok(expected));


        String actual = dataScienceAPIService.getMetrics();

        assertEquals(expected, actual);
    }

    @Test
    public void getMetrics_shouldThrow() {
        when(restTemplate.getForEntity(anyString(),
                ArgumentMatchers.<Class<String>>any()))
                .thenAnswer((Answer<String>) invocation -> {
                    throw new Exception();
                });

        String actual = "shouldBeSetToNull";

        try {
            actual = dataScienceAPIService.getMetrics();
        } catch (Exception e) {
            assertNull(actual);
        }
    }
}
