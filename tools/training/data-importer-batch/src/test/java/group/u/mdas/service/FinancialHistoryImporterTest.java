package group.u.mdas.service;

import group.u.mdas.model.CompanyIdentifier;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

public class FinancialHistoryImporterTest {

    private RestTemplate restTemplate;
    private FileSystemService fileSystemService;

    @Before
    public void setUp() throws Exception {
        restTemplate = mock(RestTemplate.class);
        fileSystemService = mock(FileSystemService.class);
    }

    @Test
    @Ignore
    public void shouldCreateRequestToRetrieveFinancialDataUsingKnownUrl(){
        CompanyIdentifier mock = mock(CompanyIdentifier.class);
        new FinancialHistoryImporter(restTemplate, fileSystemService).retrieve(mock);

    }


}
