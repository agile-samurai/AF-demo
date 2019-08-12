package group.u.mdas.web;

import group.u.mdas.models.entity.TextClassificationMongo;
import group.u.mdas.models.entity.TextClassificationElasticsearch;
import group.u.mdas.models.entity.TextClassificationPostgres;
import group.u.mdas.repository.TextClassificationRepositoryElasticsearch;
import group.u.mdas.repository.TextClassificationRepositoryMongo;
import group.u.mdas.repository.TextClassificationRepositoryPostgres;
import group.u.mdas.service.DataScienceAPIService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HelloWorldControllerTest {

    private RestTemplate restTemplate;
    private HelloWorldController helloWorldController;

    @Before
    public void setUp() {
        restTemplate = mock(RestTemplate.class);
        TextClassificationRepositoryPostgres textClassificationRepositoryPostgres = mock(TextClassificationRepositoryPostgres.class);
        TextClassificationRepositoryMongo textClassificationRepositoryMongo = mock(TextClassificationRepositoryMongo.class);
        TextClassificationRepositoryElasticsearch textClassificationRepositoryElasticsearch = mock(TextClassificationRepositoryElasticsearch.class);
        when(textClassificationRepositoryPostgres.save(any()))
                .thenReturn(new TextClassificationPostgres("text 2",
                        "comparison text 2",
                        "0.81"));
        when(textClassificationRepositoryPostgres.findById(any()))
                .thenReturn(Optional.of(new TextClassificationPostgres("text 1",
                        "comparison text 1",
                        "0.23")));
        when(textClassificationRepositoryElasticsearch.save(any()))
                .thenReturn(new TextClassificationElasticsearch("text 1",
                        "comparison text 1",
                        "0.40"));

        String url = "http://testUrl";
        DataScienceAPIService dataScienceAPIService = new DataScienceAPIService(restTemplate,
                url,
                textClassificationRepositoryPostgres,
                textClassificationRepositoryMongo, textClassificationRepositoryElasticsearch);

        when(textClassificationRepositoryMongo.save(any(TextClassificationMongo.class))).thenReturn(
                new TextClassificationMongo("text 1",
                        "comparison text 1",
                        "3.0"));
        helloWorldController = new HelloWorldController(dataScienceAPIService);
    }

    @Test
    public void getHelloWorld() {
        String start = "0.23";
        String expected = "0.233.00.40";
        when(restTemplate.postForEntity(anyString(),
                any(HttpEntity.class),
                ArgumentMatchers.<Class<String>>any()))
                .thenReturn(ResponseEntity.ok(start));

        String actual = helloWorldController.getHelloWorld();

        assertEquals(expected, actual);
    }

    @Test
    public void getMetrics() {
        String expected = "{'Model 1': 10, 'Model 2': 100, 'Model 3': 1000}";
        when(restTemplate.getForEntity(anyString(),
                ArgumentMatchers.<Class<String>>any()))
                .thenReturn(ResponseEntity.ok(expected));

        String actual = helloWorldController.getMetrics();

        assertEquals(expected, actual);
    }
}
