package group.u.records.ds;

import group.u.records.content.Dossier;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AutoRedactClientTest {

    @Test
    public void classifyAKnownEntity(){
        RestTemplate template = new RestTemplate();
        AutoRedactClient client = new AutoRedactClient(template, "foo");
        Dossier dossier = mock(Dossier.class);
        when(dossier.getSummary()).thenReturn("Pastafarians are smarter than people with Coca Cola bottles.");
        List<EntityClassification> entityClassifications = client.classify(dossier);

        assertThat(entityClassifications).hasSize(2);
    }

}
