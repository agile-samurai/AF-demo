package group.u.records.security;

import group.u.records.security.requests.EncryptionRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

public class AWSCloudHSMSecurityGatewayClient implements SecurityGatewayClient{
    private RestTemplate restTemplate;
    private String host;


    public AWSCloudHSMSecurityGatewayClient(RestTemplate restTemplate,
                                            @Value("${app.content.security.host}") String host ){
        this.restTemplate = restTemplate;
        this.host = host;
    }

    @Override
    public String encrypt(UUID id, String content) {
        return restTemplate.exchange(host + "/encrypt", HttpMethod.POST,
                new HttpEntity<>(new EncryptionRequest(id.toString(), content)),String.class).getBody();
    }

    @Override
    public String decrypt(UUID id, String dossierEncryptedContent) {
        return restTemplate.exchange(host + "/decrypt", HttpMethod.POST,
                new HttpEntity<>(new EncryptionRequest(id.toString(), dossierEncryptedContent)),String.class).getBody();
    }

    @Override
    public void delete(UUID dossierId) {

    }

}
