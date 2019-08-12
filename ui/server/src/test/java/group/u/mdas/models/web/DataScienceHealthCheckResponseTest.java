package group.u.mdas.models.web;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DataScienceHealthCheckResponseTest {
    @Test
    public void shouldHaveProperlyFunctioningNoArgsConstructor() {
        DataScienceHealthCheckResponse companySearchResult = new DataScienceHealthCheckResponse();

        assertNull(companySearchResult.getMessage());
    }

    @Test
    public void shouldHaveProperlyFunctioningGetterAndSetter() {
        String expectedMessage = "mockMessage";
        DataScienceHealthCheckResponse companySearchResult = new DataScienceHealthCheckResponse();
        assertNull(companySearchResult.getMessage());

        companySearchResult.setMessage(expectedMessage);

        assertEquals(expectedMessage, companySearchResult.getMessage());
    }
}