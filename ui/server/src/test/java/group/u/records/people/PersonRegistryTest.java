package group.u.records.people;

import group.u.records.models.Person;
import group.u.records.models.entity.MovieDetail;
import group.u.records.models.entity.MovieTitle;
import group.u.records.repository.PersonRepository;
import group.u.records.service.LevenshteinDistanceService;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PersonRegistryTest {

    @Test
    public void shouldMergeActorsWithSimilarNames(){
        PersonRepository personRepository = mock(PersonRepository.class);
        PersonRegistry pr = new PersonRegistry(personRepository, new LevenshteinDistanceService());
        Person realCarlyle = new Person("/name/nm4360085/", "Kevin Hart", new HashSet<>(), new HashSet<>());
        MovieDetail movie1 = mock(MovieDetail.class);
        when(movie1.getName()).thenReturn("Movie 1");
        when(movie1.getId()).thenReturn(UUID.randomUUID());
        MovieDetail movie2 = mock(MovieDetail.class);
        when(movie2.getName()).thenReturn("Movie 2");
        when(movie2.getId()).thenReturn(UUID.randomUUID());
        pr.reconcile(realCarlyle, movie1);
        pr.reconcile( new Person("/name/nm4360085/", "Kevin D. Hart", new HashSet<>(), new HashSet<>()), movie2);

        MovieTitle expectedMovie1 = MovieTitle.from(movie1);
        MovieTitle expectedMovie2 = MovieTitle.from(movie2);

        assertEquals(2, realCarlyle.getTitles().size());
        assertEquals(expectedMovie1.getName(), realCarlyle.getTitles().toArray(new MovieTitle[0])[0].getName());
        assertEquals(expectedMovie2.getName(), realCarlyle.getTitles().toArray(new MovieTitle[1])[1].getName());
    }

    @Test
    public void shouldMergeAliasesFromMovieCharacters(){

    }

    @Test
    public void shouldNotMergeAliasesFromMovieCharacters(){

    }
}
