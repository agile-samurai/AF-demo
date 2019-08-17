package group.u.records.service;

import group.u.records.content.Dossier;

import java.util.UUID;

public interface DataService {
    void save(UUID dossierId, String dossierEncryptedContent);
    String get(UUID dossierid );
    void delete(UUID dossierId );
}
