package group.u.records.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import group.u.records.config.MovieDetailsDataSourceManager;
import group.u.records.models.entity.MovieDetail;
import group.u.records.models.entity.MoviePublicSummary;
import group.u.records.people.PersonRegistry;
import group.u.records.repository.PersonRepository;
import group.u.records.repository.MoviePublicSummaryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class EntertainmentDetailsService {

    private PersonRepository personRepository;
    private S3DataService dataService;
    private DossierBuilderService dossierBuilderService;
    private Logger logger = LoggerFactory.getLogger(EntertainmentDetailsService.class);
    private MoviePublicSummaryRepository moviePublicSummaryRepository;
    private MovieListIdentifierProvider identifierProvider;
    private PersonRegistry personRegistry;
    private MovieDetailsDataSourceManager dataSourceManager;

    public EntertainmentDetailsService(PersonRepository personRepository,
                                       S3DataService dataService,
                                       DossierBuilderService dossierBuilderService,
                                       MoviePublicSummaryRepository moviePublicSummaryRepository,
                                       MovieListIdentifierProvider identifierProvider,
                                       PersonRegistry personRegistry,
                                       MovieDetailsDataSourceManager dataSourceManager ){
        this.personRepository = personRepository;
        this.dataService = dataService;
        this.dossierBuilderService = dossierBuilderService;
        this.moviePublicSummaryRepository = moviePublicSummaryRepository;
        this.identifierProvider = identifierProvider;
        this.personRegistry = personRegistry;
        this.dataSourceManager = dataSourceManager;

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true );
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false );


    }

    public void loadMovieDetails() {
        personRepository.deleteAll();
        moviePublicSummaryRepository.deleteAll();

        for(MovieIdentifier id : identifierProvider.getMovieIdentifiers() ){
            logger.debug("Parsing movie: " + id );
            List<MovieDetail> movieDetails = new ArrayList();
            for(MovieDetailsDataSource dataSource : dataSourceManager.getDataSources()){

                logger.debug("Retrieving from data source:  "+ dataSource.getLineage());
                try {
                    MovieDetail movieDetail = dataSource.getMovieDetails(id.getImdbId());
                    if (movieDetail != null) {
                        moviePublicSummaryRepository.save(new MoviePublicSummary(movieDetail));
                        movieDetails.add(movieDetail);
                    }
                }catch( Exception e ){
                    logger.debug( "Exception while parsing imdb - " + e );
                    e.printStackTrace();
                    logger.debug("Movie data was not present with provider:  " + dataSource + " - " + id );
                }
            }

            logger.debug("done loading movie:  "+id);
            personRegistry.reconcile(movieDetails);
            dossierBuilderService.generateDossiers(movieDetails, id);
        }

    }

}
