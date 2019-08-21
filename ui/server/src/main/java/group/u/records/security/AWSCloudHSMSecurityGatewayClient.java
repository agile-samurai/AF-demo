package group.u.records.security;

import group.u.records.security.requests.EncryptionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;

import java.util.UUID;

public class AWSCloudHSMSecurityGatewayClient implements SecurityGatewayClient {
    private RestTemplate restTemplate;
    private String host;
    private Logger logger = LoggerFactory.getLogger(AWSCloudHSMSecurityGatewayClient.class);

    public AWSCloudHSMSecurityGatewayClient(RestTemplate restTemplate,
                                            @Value("${app.content.security.host}") String host) {
        this.restTemplate = restTemplate;
        this.host = host;
    }

    @Override
    public String encrypt(UUID id, String content) {
        try {
            return restTemplate.exchange(host + "/encrypt", HttpMethod.POST,
                    new HttpEntity<>(new EncryptionRequest(id.toString(), content)), String.class).getBody();
        } catch (Exception e) {
            logger.error("Error identified while encrypting:  " + id.toString());
        }

        return "";
    }

    @Override
    public String decrypt(UUID id, String dossierEncryptedContent) {
        try {
            return restTemplate.exchange(host + "/decrypt", HttpMethod.POST,
                    new HttpEntity<>(new EncryptionRequest(id.toString(), dossierEncryptedContent)), String.class).getBody();
        } catch (Exception e) {
            logger.error("Error identified while decryption:  " + id.toString());
        }

        return "";
    }

    @Override
    public void delete(UUID id) {
        try {
            restTemplate.delete(host + "/" + id.toString());
        } catch (Exception e) {
            logger.error("Error identified while deleting:  " + id.toString());
        }
    }

}
