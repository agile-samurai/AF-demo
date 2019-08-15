package group.u.records.service;

import group.u.records.content.Dossier;
import group.u.records.content.Genre;
import group.u.records.ds.MovieSimilarityProvider;
import group.u.records.ds.GenreDistributionImageProvider;
import group.u.records.ds.PredictiveAutoRedactProvider;
import group.u.records.models.MovieDetail;
import group.u.records.security.DossierRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static java.util.Arrays.asList;

@Service
public class DossierBuilderService {

    private Logger logger = LoggerFactory.getLogger(DossierBuilderService.class);
    private PredictiveAutoRedactProvider autoRedactProvider;
    private final MovieSimilarityProvider scoringProvider;
    private final GenreDistributionImageProvider imageProvider;
    private DossierRepository dossierRepository;

    public DossierBuilderService(PredictiveAutoRedactProvider autoRedactProvider,
                                 MovieSimilarityProvider scoringProvider,
                                 GenreDistributionImageProvider imageProvider,
                                 DossierRepository dossierRepository ) {
        this.autoRedactProvider = autoRedactProvider;
        this.scoringProvider = scoringProvider;
        this.imageProvider = imageProvider;
        this.dossierRepository = dossierRepository;
    }


    public void generateDossier(MovieDetail movieDetail) {
        Dossier dossier = new Dossier(UUID.randomUUID(), movieDetail.getName(), movieDetail.getSummary(), asList(new Genre(movieDetail.getGenre(),
                imageProvider.getJson())));
        logger.debug("Generating dossier for:  "  + movieDetail);
        dossierRepository.save(dossier);
    }
}
