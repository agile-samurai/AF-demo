package group.u.records.service;

import group.u.records.ds.providers.MovieSimilarityProvider;
import group.u.records.ds.providers.GenreDistributionImageProvider;
import group.u.records.ds.providers.PredictiveAutoRedactProvider;
import group.u.records.security.MasterDossierService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class DossierBuilderServiceTest {

    private PredictiveAutoRedactProvider autoRedactProvider;
    private MovieSimilarityProvider scoringProvider;
    private GenreDistributionImageProvider imageProvider;

    @Before
    public void setup() {
        autoRedactProvider = mock(PredictiveAutoRedactProvider.class);
        scoringProvider = mock(MovieSimilarityProvider.class);
        imageProvider = mock(GenreDistributionImageProvider.class);
    }

    @Test
    @Ignore
    public void shouldGenerateADossierGivenAMovieDetail() {
        DossierBuilderService builderService =
                new DossierBuilderService(autoRedactProvider, scoringProvider,
                imageProvider, mock(MasterDossierService.class));
    }

}
