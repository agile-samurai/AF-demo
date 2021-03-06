package group.u.records.security;

import group.u.records.models.entity.MoviePublicSummary;
import group.u.records.repository.MoviePublicSummaryRepository;
import group.u.records.service.datamanagement.DataService;
import group.u.records.service.dossier.models.MasterDossier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.apache.commons.codec.binary.Base64.decodeBase64;
import static org.apache.commons.codec.binary.Base64.encodeBase64String;

@Component
public class MasterDossierService {
    private DossierEncryptionService dossierEncryptionService;
    private MoviePublicSummaryRepository publicSummaryRepository;
    private DataService dataService;
    private Logger logger = LoggerFactory.getLogger(MasterDossierService.class);

    public MasterDossierService(DossierEncryptionService dossierEncryptionService,
                                MoviePublicSummaryRepository publicSummaryRepository,
                                DataService dataService) {
        this.dossierEncryptionService = dossierEncryptionService;
        this.publicSummaryRepository = publicSummaryRepository;
        this.dataService = dataService;
    }

    public void save(MasterDossier dossier) {
        dataService.save(dossier.getId(), dossierEncryptionService.encrypt(dossier) );
    }

    public void delete(UUID dossierId){
        MasterDossier masterDossier = get(dossierId);
        List<UUID> fileIds = masterDossier.getDossierFileInfos().stream().map(DossierFileInfo::getFileId).collect(Collectors.toList());
        updateDeletePublicSummary(dossierId);
        dataService.delete(dossierId, fileIds);
        this.dossierEncryptionService.delete(dossierId);
    }

    private void updateDeletePublicSummary(UUID dossierId) {
        MoviePublicSummary moviePublicSummary = publicSummaryRepository.findById(dossierId).get();
        moviePublicSummary.setDossierAvailable(false);
        publicSummaryRepository.save(moviePublicSummary);
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

    public DossierFileInfo saveFile(UUID dossierId, MultipartFile file ) throws IOException {
        String fileEncrypted = dossierEncryptionService.encryptFile(dossierId, encodeBase64String(file.getBytes()));
        UUID fileId = UUID.randomUUID();
        dataService.saveFile(fileId, fileEncrypted);
        DossierFileInfo dossierFileInfo = new DossierFileInfo(fileId, file.getOriginalFilename(), file.getContentType());

        updateFileInfos(dossierId, dossierFileInfo);
        return dossierFileInfo;
    }

    private void updateFileInfos(UUID dossierId, DossierFileInfo dossierFileInfo) {
        MasterDossier masterDossier = get(dossierId);
        masterDossier.addFileInfo(dossierFileInfo);
        save(masterDossier);
    }

    public byte[] getFile(UUID dossierId, UUID fileId ) {
        String fileContent = dataService.getFile(fileId);
        String fileEncrypted = dossierEncryptionService.decryptFile(dossierId, fileContent);

        return decodeBase64(fileEncrypted);
    }
}
