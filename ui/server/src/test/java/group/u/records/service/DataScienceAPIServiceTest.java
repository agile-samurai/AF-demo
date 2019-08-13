package group.u.records.service;

import group.u.records.models.web.DataScienceHealthCheckResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class DataScienceAPIServiceTest {

    private RestTemplate restTemplate;
    private DataScienceAPIService dataScienceAPIService;

    @Before
    public void setUp() {
        restTemplate = mock(RestTemplate.class);

        String url = "http://testUrl";
        dataScienceAPIService = new DataScienceAPIService(restTemplate, url);
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
