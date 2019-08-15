package group.u.records.service;

import group.u.records.ds.DataScienceScoringProvider;
import group.u.records.ds.GenreDistributionImageProvider;
import group.u.records.ds.PredictiveAutoRedactProvider;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class DossierBuilderServiceTest {

    private PredictiveAutoRedactProvider autoRedactProvider;
    private DataScienceScoringProvider scoringProvider;
    private GenreDistributionImageProvider imageProvider;

    @Before
    public void setup() {
        autoRedactProvider = mock(PredictiveAutoRedactProvider.class);
        scoringProvider = mock(DataScienceScoringProvider.class);
        imageProvider = mock(GenreDistributionImageProvider.class);
    }

    @Test
    public void shouldGenerateADossierGivenAMovieDetail() {
        DossierBuilderService builderService =
                new DossierBuilderService(autoRedactProvider, scoringProvider,
                imageProvider);
    }

}
