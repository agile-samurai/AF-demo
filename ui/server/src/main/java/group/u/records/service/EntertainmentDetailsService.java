package group.u.records.service;

import com.github.javafaker.Faker;
import group.u.records.models.Actor;
import group.u.records.models.MovieDetails;
import group.u.records.repository.ActorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class EntertainmentDetailsService {

    private ActorRepository actorRepository;
    private Logger logger = LoggerFactory.getLogger(EntertainmentDetailsService.class);

    public EntertainmentDetailsService(ActorRepository actorRepository){
        this.actorRepository = actorRepository;
    }

    public List<MovieDetails> getMovieDetails(String movieId){
        List<MovieDetails> results = new ArrayList();
        for( int i = 0; i < 100; i++ ){
            results.add(generateMovieDetail());
        }
        return results;
    }

    private MovieDetails generateMovieDetail() {
        ArrayList actors = new ArrayList();
        for( int i = 0; i < new Random().nextInt(30); i++ ){
            Actor actor = new Actor(Faker.instance().name().fullName());
            logger.debug("Actor:  " + actor );
            actors.add(actor);
        }
        return new MovieDetails(
                Faker.instance().ancient().god() + " & the " + Faker.instance().ancient().hero(),
                actors,
                Faker.instance().lorem().paragraph(),
                Faker.instance().lorem().words(new Random().nextInt(10)),
                "PG-13",
                LocalDate.now()
        );
    }

    public void loadMovieDetails() {
        for( MovieDetails detail : getMovieDetails(null)){
            logger.debug("Building details for movie:  " + detail.getName());
            detail.getActors().forEach(actorRepository::save);
        }
    }
}
