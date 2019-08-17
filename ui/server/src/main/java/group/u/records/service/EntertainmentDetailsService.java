package group.u.records.service;

import group.u.records.repository.ActorRepository;
import group.u.records.repository.MoviePublicSummaryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EntertainmentDetailsService {

    private ActorRepository actorRepository;
    private S3DataService dataService;
    private DossierBuilderService dossierBuilderService;
    private Logger logger = LoggerFactory.getLogger(EntertainmentDetailsService.class);
    private MoviePublicSummaryRepository moviePublicSummaryRepository;

    public EntertainmentDetailsService(ActorRepository actorRepository,
                                       S3DataService dataService,
                                       DossierBuilderService dossierBuilderService,
                                       MoviePublicSummaryRepository moviePublicSummaryRepository){
        this.actorRepository = actorRepository;
        this.dataService = dataService;
        this.dossierBuilderService = dossierBuilderService;
        this.moviePublicSummaryRepository = moviePublicSummaryRepository;
    }


    public void loadMovieDetails() {
        dataService.processMovies(actorRepository, moviePublicSummaryRepository, dossierBuilderService );
    }


}
