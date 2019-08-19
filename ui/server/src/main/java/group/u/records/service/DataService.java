package group.u.records.service;

import java.util.List;
import java.util.UUID;

public interface DataService {
    void save(UUID dossierId, String dossierEncryptedContent);
    String get(UUID dossierid );
    void delete(UUID dossierId, List<UUID> fileIds);
    void saveFile(UUID fileId, String fileEncrypted);
    String getFile(UUID fileId);
}
