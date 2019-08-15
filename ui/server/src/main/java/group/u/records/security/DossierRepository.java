package group.u.records.security;

import group.u.records.content.Dossier;
import group.u.records.service.DataService;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DossierRepository {
    private DossierEncryptionService dossierEncryptionService;
    private DataService dataService;

    public DossierRepository(DossierEncryptionService dossierEncryptionService, DataService dataService) {
        this.dossierEncryptionService = dossierEncryptionService;
        this.dataService = dataService;
    }

    public void save(Dossier dossier) {
        dataService.save(dossier.getId(), dossierEncryptionService.encrypt(dossier) );
    }

    public void delete(UUID dossierId){}

    public Dossier get(UUID id) {
        return dossierEncryptionService.decrypt(id, dataService.get(id));
    }
}
