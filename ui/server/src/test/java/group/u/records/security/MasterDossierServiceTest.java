package group.u.records.security;

import group.u.records.repository.MoviePublicSummaryRepository;
import group.u.records.service.datamanagement.S3DataService;
import org.junit.Ignore;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class MasterDossierServiceTest {

    @Test
    @Ignore
    public void shouldEncryptDossier(){
        MasterDossierService repository = new MasterDossierService(mock(DossierEncryptionService.class), mock(MoviePublicSummaryRepository.class), mock(S3DataService.class));
    }

}
