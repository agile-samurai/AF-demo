package group.u.records.security;

import group.u.records.content.Dossier;
import group.u.records.service.DataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class DossierRepository {
    private DossierEncryptionService dossierEncryptionService;
    private DataService dataService;
    private Logger logger = LoggerFactory.getLogger(DossierRepository.class);

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

    public void addNote(UUID id, String name, String note) {
        Dossier dossier = get(id);
        dossier.addNote(LocalDateTime.now(), name, note);
        logger.debug("Saving note in dossier from user:  " + name );
    }
}
