package group.u.records.service;

import group.u.records.content.Dossier;
import group.u.records.content.Genre;
import group.u.records.ds.providers.GenreDistributionImageProvider;
import group.u.records.ds.providers.MovieSimilarityProvider;
import group.u.records.ds.providers.PredictiveAutoRedactProvider;
import group.u.records.models.entity.MovieDetail;
import group.u.records.security.MasterDossierRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

@Service
public class DossierBuilderService {

    private Logger logger = LoggerFactory.getLogger(DossierBuilderService.class);
    private PredictiveAutoRedactProvider autoRedactProvider;
    private final MovieSimilarityProvider scoringProvider;
    private final GenreDistributionImageProvider imageProvider;
    private MasterDossierRepository masterDossierRepository;

    public DossierBuilderService(PredictiveAutoRedactProvider autoRedactProvider,
                                 MovieSimilarityProvider scoringProvider,
                                 GenreDistributionImageProvider imageProvider,
                                 MasterDossierRepository masterDossierRepository) {
        this.autoRedactProvider = autoRedactProvider;
        this.scoringProvider = scoringProvider;
        this.imageProvider = imageProvider;
        this.masterDossierRepository = masterDossierRepository;
    }


    public MasterDossier generateDossiers(List<MovieDetail> movieDetails, UUID id ){
        MasterDossier masterDossier = new MasterDossier(movieDetails
                .stream()
                .map(f -> generateDossier(f))
                .collect(toList()), id);
        masterDossierRepository.save(masterDossier);

        logger.debug("About to save dossier:  " + id.toString());
        return masterDossier;

    }

    public Dossier generateDossier(MovieDetail movieDetail) {
        Dossier dossier = new Dossier(movieDetail.getId(), movieDetail.getName(), movieDetail.getSummary(), asList(new Genre(movieDetail.getGenre(),
                imageProvider.getJson(movieDetail.getId()))), movieDetail.getLineage());
        dossier.setRedactionSuggestions(autoRedactProvider.redact(dossier));
        logger.debug("Generating dossier for:  "  + movieDetail);
//        masterDossierRepository.save(dossier);

        return dossier;
    }
}
