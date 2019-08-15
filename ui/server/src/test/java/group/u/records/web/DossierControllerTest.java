package group.u.records.web;

import group.u.records.security.DossierRepository;
import group.u.records.service.S3DataService;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class DossierControllerTest {

    @Test
    public void shouldLoadADossier(){
        DossierController controller = new DossierController(null);
    }

}
