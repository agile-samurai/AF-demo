package group.u.records.service;

import group.u.records.models.MovieDetails;
import group.u.records.repository.ActorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EntertainmentDetailsService {

    private ActorRepository actorRepository;
    private S3DataService dataService;
    private Logger logger = LoggerFactory.getLogger(EntertainmentDetailsService.class);

    public EntertainmentDetailsService(ActorRepository actorRepository, S3DataService dataService ){
        this.actorRepository = actorRepository;
        this.dataService = dataService;
    }

    public void loadMovieDetails() {
        dataService.loadAllMovies(actorRepository);
    }
}
