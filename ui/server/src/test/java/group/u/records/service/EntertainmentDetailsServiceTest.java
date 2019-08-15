package group.u.records.service;

import group.u.records.repository.ActorRepository;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class EntertainmentDetailsServiceTest {

    @Test
    public void shouldGenerateADossierForAGivenMovie(){
        EntertainmentDetailsService detailsService = new EntertainmentDetailsService(mock(ActorRepository.class), mock(S3DataService.class), mock(DossierBuilderService.class));
    }

}
