package group.u.records.service.dossier;

import group.u.records.datascience.providers.GenreDistributionImageProvider;
import group.u.records.datascience.providers.MovieSimilarityProvider;
import group.u.records.datascience.providers.PredictiveAutoRedactProvider;
import group.u.records.datasource.TwitterMovieDataSource;
import group.u.records.models.MovieDetail;
import group.u.records.security.MasterDossierService;
import group.u.records.service.MovieIdentifier;
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
    private TwitterMovieDataSource twitterMovieDataSource;
    private MasterDossierService masterDossierService;

    public DossierBuilderService(PredictiveAutoRedactProvider autoRedactProvider,
                                 MovieSimilarityProvider scoringProvider,
                                 GenreDistributionImageProvider imageProvider,
                                 TwitterMovieDataSource twitterMovieDataSource,
                                 MasterDossierService masterDossierService) {
        this.autoRedactProvider = autoRedactProvider;
        this.scoringProvider = scoringProvider;
        this.imageProvider = imageProvider;
        this.twitterMovieDataSource = twitterMovieDataSource;
        this.masterDossierService = masterDossierService;
    }


    public MasterDossier generateDossiers(List<MovieDetail> movieDetails, MovieIdentifier movieId) {
        logger.debug("About to write dossier with lineage count:  " + movieDetails.size());
        UUID id = UUID.nameUUIDFromBytes(movieId.getImdbId().getBytes());
        MasterDossier masterDossier = new MasterDossier(movieDetails
                .stream()
                .map(f -> generateDossier(f))
                .collect(toList()), scoringProvider.getSimilarMovies(movieId.getImdbId()), movieId,
                imageProvider.getJson(movieId.getImdbId()),
                twitterMovieDataSource.getMovieDetails(movieId));

        masterDossierService.save(masterDossier);
        logger.debug("About to save dossier:  " + id.toString());
        return masterDossier;
    }

    public Dossier generateDossier(MovieDetail movieDetail) {
        Dossier dossier = new Dossier(movieDetail.getId(),
                movieDetail.getName(),
                movieDetail.getSummary(),
                movieDetail.getCharacters(),
                movieDetail.getReviews(),
                asList(new Genre(movieDetail.getGenre())),
                movieDetail.getLineage());
        dossier.setRedactionSuggestions(autoRedactProvider.redact(dossier));

        logger.debug("Generating dossier for:  " + movieDetail);
        return dossier;
    }
}
