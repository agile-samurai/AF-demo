package group.u.records.web.core;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.*;

public class HealthCheckControllerTest {

    @Test
    public void shouldGetStatus() {
        final HealthCheckController healthCheckController = new HealthCheckController();
        final ResponseEntity<String> actual = healthCheckController.getStatus();

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals("Healthy", actual.getBody());
    }
}