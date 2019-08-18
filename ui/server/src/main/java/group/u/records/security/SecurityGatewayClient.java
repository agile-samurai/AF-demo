package group.u.records.security;

import java.util.UUID;

public interface SecurityGatewayClient {
   String encrypt(UUID id, String content);
   String decrypt(UUID id, String dossierEncryptedContent);
   void delete(UUID dossierId);
}
