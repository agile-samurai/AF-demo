package group.u.records.security;

import group.u.records.service.DataService;
import group.u.records.service.MasterDossier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class MasterDossierRepository {
    private DossierEncryptionService dossierEncryptionService;
    private DataService dataService;
    private Logger logger = LoggerFactory.getLogger(MasterDossierRepository.class);

    public MasterDossierRepository(DossierEncryptionService dossierEncryptionService, DataService dataService) {
        this.dossierEncryptionService = dossierEncryptionService;
        this.dataService = dataService;
    }

    public void save(MasterDossier dossier) {
        dataService.save(dossier.getId(), dossierEncryptionService.encrypt(dossier) );
    }

    public void delete(UUID dossierId){
        dataService.delete(dossierId);
        this.dossierEncryptionService.delete(dossierId);
    }

    public MasterDossier get(UUID id) {
        return dossierEncryptionService.decrypt(id, dataService.get(id));
    }

    public void addNote(UUID id, String name, String note) {
        MasterDossier dossier = get(id);
        dossier.addNote(LocalDateTime.now().toString(), name, note);
        logger.debug("Saving note in dossier from user:  " + name );
        save(dossier);
    }
}
