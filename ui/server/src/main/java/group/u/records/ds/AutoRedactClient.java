package group.u.records.ds;

import com.fasterxml.jackson.databind.ObjectMapper;
import group.u.records.content.Dossier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

@Component
public class AutoRedactClient {

    private RestTemplate template;
    private String hostUrl;

    public AutoRedactClient(RestTemplate template, @Value("${app.ds.redact.host}") String hostUrl ) {
        this.template = template;
        this.hostUrl = hostUrl;
    }

    public List<EntityClassification> classify(Dossier dossier) {
        SpacyRequest spacyRequest = new SpacyRequest(dossier.getSummary());
        HttpEntity<SpacyRequest> request = new HttpEntity(spacyRequest);

        String response = template.exchange(hostUrl + "/ent", HttpMethod.POST, request, String.class).getBody();
        try {
            return asList(new ObjectMapper().readValue(response, EntityClassification[].class));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList();
    }
}
