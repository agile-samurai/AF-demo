package group.u.records.service;

import java.util.UUID;

public interface DataService {
    void save(UUID dossierId, String dossierEncryptedContent);
    String get(UUID dossierid );
    void delete(UUID dossierId );
}
