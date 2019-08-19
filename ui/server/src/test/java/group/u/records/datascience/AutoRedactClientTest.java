package group.u.records.datascience;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import group.u.records.service.dossier.Dossier;
import group.u.records.datascience.providers.AutoRedactClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AutoRedactClientTest {

    private static final String TEST_HOST = "http://foo";
    private ObjectMapper objectMapper;
    private Dossier dossier;
    private RestTemplate template;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        dossier = mock(Dossier.class);
        when(dossier.getSummary()).thenReturn("Pastafarians are smarter than people with Coca Cola bottles.");
        template = mock(RestTemplate.class);
    }

    @Test
    public void classifyAKnownEntity() throws JsonProcessingException {
        ArgumentCaptor<HttpEntity> spacyCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        ResponseEntity response = mock(ResponseEntity.class);

        when(response.getBody()).thenReturn(objectMapper.writeValueAsString(
                asList(new EntityClassification(0,0,"foo","foo"))));
        when(template.exchange( eq(TEST_HOST+"/ent"), eq(HttpMethod.POST),
                spacyCaptor.capture(), eq(String.class))).thenReturn(response);

        AutoRedactClient client = new AutoRedactClient(objectMapper, template, TEST_HOST);
        List<EntityClassification> entityClassifications = client.classify(dossier);
        assertThat(entityClassifications).hasSize(1);
    }

    @Test
    public void shouldHandleErrorCaseGracefully(){
        AutoRedactClient client = new AutoRedactClient(objectMapper, template, TEST_HOST);
        when(template.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class))).thenThrow(NullPointerException.class);
        List<EntityClassification> classify = client.classify(new Dossier());

        assertThat(classify).isEmpty();
    }


}
