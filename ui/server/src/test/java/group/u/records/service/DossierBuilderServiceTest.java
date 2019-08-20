package group.u.records.service;

import group.u.records.datascience.providers.MovieSimilarityProvider;
import group.u.records.datascience.providers.GenreDistributionImageProvider;
import group.u.records.datascience.providers.PredictiveAutoRedactProvider;
import group.u.records.datasource.TwitterMovieDataSource;
import group.u.records.security.MasterDossierService;
import group.u.records.service.dossier.DossierBuilderService;
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
                imageProvider, mock(TwitterMovieDataSource.class), mock(MasterDossierService.class));
    }

}
