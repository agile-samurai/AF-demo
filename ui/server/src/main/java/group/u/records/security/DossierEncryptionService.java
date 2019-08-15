package group.u.records.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import group.u.records.content.Dossier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Service
public class DossierEncryptionService {

    private Logger logger = LoggerFactory.getLogger(DossierEncryptionService.class);
    private ObjectMapper objectMapper;
    private HSMGatewayClient gatewayClient;

    public DossierEncryptionService(ObjectMapper objectMapper, HSMGatewayClient gatewayClient ){
        this.objectMapper = objectMapper;
        this.gatewayClient = gatewayClient;
    }

    public String encrypt(Dossier dossier) {
        try {
            return gatewayClient.encrypt(objectMapper.writeValueAsString(dossier));
        } catch (JsonProcessingException e) {
            logger.debug("Error while serializing dossier:  " + dossier.getId());
        }
        return "";
    }

    public Dossier decrypt(UUID id, String encryptedContent) {
        try {
            return objectMapper.readValue(gatewayClient.decrypt(id,encryptedContent), Dossier.class);
        } catch (IOException e) {
            //Todo: Log user access explicitly.
            throw new SecurityException( "Unauthorized access to dossier" );

        }
    }
}
