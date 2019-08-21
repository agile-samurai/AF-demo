package group.u.records.config;

import group.u.records.repository.MoviePublicSummaryRepository;
import group.u.records.repository.PersonRepository;
import group.u.records.repository.TrainingDataRepository;
import group.u.records.service.EntertainmentDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class EnvironmentInitializer {

    private EntertainmentDetailsService eds;
    private MoviePublicSummaryRepository moviePublicSummaryRepository;
    private PersonRepository personRepository;
    private TrainingDataRepository trainingDataRepository;
    private Logger logger = LoggerFactory.getLogger(EnvironmentInitializer.class);

    public EnvironmentInitializer(EntertainmentDetailsService eds,
                                  MoviePublicSummaryRepository moviePublicSummaryRepository,
                                  PersonRepository personRepository,
                                  TrainingDataRepository trainingDataRepository){
        this.eds = eds;
        this.moviePublicSummaryRepository = moviePublicSummaryRepository;
        this.personRepository = personRepository;
        this.trainingDataRepository = trainingDataRepository;
    }

    @PostConstruct
    private void init(){
        moviePublicSummaryRepository.deleteAll();
        personRepository.deleteAll();
        trainingDataRepository.deleteAll();

        ExecutorService executor = Executors.newFixedThreadPool(10);
        executor.submit(() -> {
            try {
                eds.loadMovieDetails();
            }catch( Exception e ){
                logger.error( "Exception while loading:  ", e );
            }
        });
    }
}
