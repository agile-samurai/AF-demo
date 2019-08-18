package group.u.records.security;

import java.util.UUID;

public class InMemorySecurityClient implements SecurityGatewayClient {
    public String encrypt(UUID id, String content) {
        return content;
    }
    public String decrypt(UUID id, String dossierEncryptedContent) {
        return dossierEncryptedContent;
    }
    public void delete(UUID dossierId) { }
}
