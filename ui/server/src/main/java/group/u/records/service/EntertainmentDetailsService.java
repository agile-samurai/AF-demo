package group.u.records.service;

import com.github.javafaker.Faker;
import group.u.records.models.Actor;
import group.u.records.models.MovieDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class EntertainmentDetailsService {

    public List<MovieDetails> getMovieDetails(String movieId){
        List<MovieDetails> results = new ArrayList();
        for( int i = 0; i < 1000; i++ ){
            results.add(generateMovieDetail());
        }
        return results;
    }

    private MovieDetails generateMovieDetail() {
        ArrayList actors = new ArrayList();
        for( int i = 0; i < new Random().nextInt(30); i++ ){
            actors.add( new Actor(Faker.instance().name().fullName()));
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

}
