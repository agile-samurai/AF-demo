package group.u.records.ds.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import group.u.records.content.Dossier;
import group.u.records.ds.EntityClassification;
import group.u.records.ds.SpacyRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

@Component
public class AutoRedactClient {

    private RestTemplate template;
    private String hostUrl;
    private ObjectMapper objectMapper;

    public AutoRedactClient(ObjectMapper objectMapper,
                            RestTemplate template,
                            @Value("${app.ds.redact.host}") String hostUrl) {
        this.objectMapper = objectMapper;
        this.template = template;
        this.hostUrl = hostUrl;
    }

    public List<EntityClassification> classify(Dossier dossier) {
        SpacyRequest spacyRequest = new SpacyRequest(dossier.getSummary());
        HttpEntity<SpacyRequest> request = new HttpEntity(spacyRequest);

        try {
            String response = template.exchange(hostUrl + "/ent", HttpMethod.POST, request, String.class).getBody();
            return asList(objectMapper.readValue(response, EntityClassification[].class));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList();
    }
}
