package group.u.records.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import group.u.records.service.dossier.MasterDossier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Service
public class DossierEncryptionService {

    private Logger logger = LoggerFactory.getLogger(DossierEncryptionService.class);
    private ObjectMapper objectMapper;
    private SecurityGatewayClient gatewayClient;

    public DossierEncryptionService(ObjectMapper objectMapper, SecurityGatewayClient gatewayClient ){
        this.objectMapper = objectMapper;
        this.gatewayClient = gatewayClient;
    }

    public String encrypt(MasterDossier dossier) {
        try {
            return gatewayClient.encrypt(dossier.getId(), objectMapper.writeValueAsString(dossier));
        } catch (JsonProcessingException e) {
            logger.debug("Error while serializing dossier:  " + dossier.getId());
        }
        return "";
    }

    public MasterDossier decrypt(UUID id, String encryptedContent) {
        try {
            return objectMapper.readValue(gatewayClient.decrypt(id,encryptedContent), MasterDossier.class);
        } catch (IOException e) {
            //Todo: Log user access explicitly.
            throw new SecurityException( "Unauthorized access to dossier" );

        }
    }

    public void delete(UUID dossierId) {
        this.gatewayClient.delete(dossierId);
    }

    public String encryptFile(UUID id, String encodeBase64String) {
        try {
            return gatewayClient.encrypt(id, encodeBase64String);
        }catch( Exception e ){
            logger.debug("Error while serializing dossier file:  " + id);
        }
        return "";
    }

    public String decryptFile(UUID id, String content) {
        return gatewayClient.decrypt(id, content);
    }
}
