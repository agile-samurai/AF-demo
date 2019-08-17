package group.u.records.security;

import group.u.records.content.Dossier;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class HSMGatewayClient {
    public String encrypt(String content) {
        return content;
    }

    public String decrypt(UUID id, String dossierEncryptedContent) {
        return dossierEncryptedContent;
    }
}
