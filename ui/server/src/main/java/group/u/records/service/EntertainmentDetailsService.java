package group.u.records.service;

import group.u.records.models.MovieDetail;
import group.u.records.repository.ActorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EntertainmentDetailsService {

    private ActorRepository actorRepository;
    private S3DataService dataService;
    private DossierBuilderService dossierBuilderService;
    private Logger logger = LoggerFactory.getLogger(EntertainmentDetailsService.class);

    public EntertainmentDetailsService(ActorRepository actorRepository,
                                       S3DataService dataService,
                                       DossierBuilderService dossierBuilderService ){
        this.actorRepository = actorRepository;
        this.dataService = dataService;
        this.dossierBuilderService = dossierBuilderService;
    }


    public void loadMovieDetails() {
        List<MovieDetail> movieDetails = dataService.loadAllMovies(actorRepository);
        movieDetails.forEach(
                dossierBuilderService::generateDossier
        );

        store(movieDetails);
    }

    public void store(List<MovieDetail> movieDetails ){

    }
}
