package group.u.records.web;

import group.u.records.datasource.entity.Movie;
import group.u.records.models.entity.MoviePublicSummary;
import group.u.records.repository.MoviePublicSummaryRepository;
import group.u.records.security.MasterDossierService;
import group.u.records.service.dossier.MasterDossier;
import group.u.records.web.entertainment.DossierController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DossierControllerTest {

    @Mock
    MasterDossierService masterDossierService;
    @Mock
    MoviePublicSummaryRepository moviePublicSummaryRepository;
    @Mock
    SecurityContext context;
    @InjectMocks
    DossierController dossierController;

    private MasterDossier.Builder masterDossier;

    @Before
    public void setUp() throws Exception {
        masterDossier = new MasterDossier.Builder();
    }

    @Test
    public void shouldGetADossier(){
        final ArgumentCaptor<UUID> idArgumentCaptor = ArgumentCaptor.forClass(UUID.class);
        final UUID id = UUID.randomUUID();

        final MasterDossier expected = masterDossier.withSimilarMovies(newArrayList()).build();
        when(masterDossierService.get(idArgumentCaptor.capture())).thenReturn(expected);

        final ResponseEntity<MasterDossier> actualEntity = dossierController.get(id);

        assertEquals(expected, actualEntity.getBody());
    }

    @Test
    public void shouldFetchPublicSummaries() {
        final UUID summaryId1 = UUID.randomUUID();
        final UUID summaryId2 = UUID.randomUUID();
        final MasterDossier expected = masterDossier.withSimilarMovies(newArrayList(summaryId1, summaryId2)).build();
        when(masterDossierService.get(any(UUID.class))).thenReturn(masterDossier.build());
        when(moviePublicSummaryRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        final ResponseEntity<MasterDossier> responseEntity = dossierController.get(UUID.randomUUID());
        verify(moviePublicSummaryRepository, times(2)).findById(any(UUID.class));
        assertEquals(newArrayList(), Objects.requireNonNull(responseEntity.getBody()).getSimilarMovieTitles());
    }

    @Test
    public void shouldEnrichWithSimilarities() {
        final UUID summaryId1 = UUID.randomUUID();
        final UUID summaryId2 = UUID.randomUUID();
        final MasterDossier expected = masterDossier.withSimilarMovies(newArrayList(summaryId1, summaryId2)).build();
        when(masterDossierService.get(any(UUID.class))).thenReturn(masterDossier.build());
        when(moviePublicSummaryRepository.findById(any(UUID.class))).thenReturn(Optional.of(new MoviePublicSummary()));

        final ResponseEntity<MasterDossier> responseEntity = dossierController.get(UUID.randomUUID());
        assertEquals(2, responseEntity.getBody().getSimilarMovieTitles().size());
    }

}
