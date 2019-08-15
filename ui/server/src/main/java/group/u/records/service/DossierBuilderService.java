package group.u.records.service;

import group.u.records.ds.DataScienceScoringProvider;
import group.u.records.ds.GenreDistributionImageProvider;
import group.u.records.ds.PredictiveAutoRedactProvider;
import group.u.records.models.MovieDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DossierBuilderService {

    Logger logger = LoggerFactory.getLogger(DossierBuilderService.class);
    private PredictiveAutoRedactProvider autoRedactProvider;

    public DossierBuilderService(PredictiveAutoRedactProvider autoRedactProvider,
                                 DataScienceScoringProvider scoringProvider,
                                 GenreDistributionImageProvider imageProvider ) {
        this.autoRedactProvider = autoRedactProvider;
    }

    public void generateDossier(MovieDetail movieDetail) {
        logger.debug("Generating dossier for:  "  + movieDetail);
    }
}
