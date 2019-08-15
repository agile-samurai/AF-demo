package group.u.records.security;

import group.u.records.content.Dossier;
import group.u.records.service.S3DataService;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DossierRepository {
    private DossierEncryptionService dossierEncryptionService;
    private S3DataService s3DataService;

    public DossierRepository(DossierEncryptionService dossierEncryptionService, S3DataService s3DataService) {
        this.dossierEncryptionService = dossierEncryptionService;
        this.s3DataService = s3DataService;
    }

    public void save(Dossier dossier) {
        s3DataService.save(dossier.getId(), dossierEncryptionService.encrypt(dossier) );
    }

    public void delete(UUID dossierId){}

}
