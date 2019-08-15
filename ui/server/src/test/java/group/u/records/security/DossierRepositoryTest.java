package group.u.records.security;

import group.u.records.service.S3DataService;
import org.junit.Ignore;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class DossierRepositoryTest {

    @Test
    @Ignore
    public void shouldEncryptDossier(){
        DossierRepository repository = new DossierRepository(mock(DossierEncryptionService.class), mock(S3DataService.class));
    }

}
