package group.u.records.security;

import group.u.records.models.entity.MoviePublicSummary;
import group.u.records.repository.MoviePublicSummaryRepository;
import group.u.records.service.datamanagement.DataService;
import group.u.records.service.datamanagement.S3DataService;
import group.u.records.service.dossier.models.MasterDossier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class MasterDossierServiceTest {

    @Test
    public void shouldEncryptDossier(){
        DossierEncryptionService encryptionService = mock(DossierEncryptionService.class);
        MoviePublicSummaryRepository publicSummaryRepository = mock(MoviePublicSummaryRepository.class);
        MasterDossierService repository = new MasterDossierService(encryptionService, publicSummaryRepository, mock(S3DataService.class));
        MasterDossier dossier = new MasterDossier();
        repository.save(dossier);

        verify(encryptionService).encrypt(dossier);
    }

    @Test
    public void shouldDeleteDossier(){
        DossierEncryptionService encryptionService = mock(DossierEncryptionService.class);
        MoviePublicSummaryRepository publicSummaryRepository = mock(MoviePublicSummaryRepository.class);
        DataService dataService = mock(S3DataService.class);
        MasterDossierService dossierService = new MasterDossierService(encryptionService, publicSummaryRepository, dataService);
        MasterDossier dossier = mock(MasterDossier.class);

        ArrayList fileInfos = new ArrayList();
        when(dossier.getDossierFileInfos()).thenReturn(fileInfos);

        UUID id = UUID.randomUUID();
        when(dossierService.get(id)).thenReturn(dossier);
        when(publicSummaryRepository.findById(any())).thenReturn(Optional.of(new MoviePublicSummary()));

        dossierService.delete(id);
        verify(dataService).delete(eq(id), anyList());
        verify(encryptionService).delete(id);
    }


}
