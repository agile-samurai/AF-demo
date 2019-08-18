package group.u.records.people;

import group.u.records.models.Person;
import group.u.records.models.entity.MovieDetail;
import group.u.records.models.entity.MovieTitle;
import group.u.records.repository.PersonRepository;
import group.u.records.service.LevenshteinDistanceService;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PersonRegistryTest {

    @Test
    public void shouldMergeActorsWithSimilarNames(){
        PersonRepository personRepository = mock(PersonRepository.class);
        PersonRegistry pr = new PersonRegistry(personRepository, new LevenshteinDistanceService());
        Person realCarlyle = new Person("Kevin Hart", new ArrayList<>(), new ArrayList<>());
        MovieDetail movie1 = mock(MovieDetail.class);
        when(movie1.getName()).thenReturn("Movie 1");
        MovieDetail movie2 = mock(MovieDetail.class);
        when(movie2.getName()).thenReturn("Movie 2");
        pr.reconcile(realCarlyle, movie1);
        pr.reconcile( new Person("Kevin D. Hart", new ArrayList<>(), new ArrayList<>()), movie2);

        List<MovieTitle> expected = List.of(MovieTitle.from(movie1), MovieTitle.from(movie2));

        assertEquals(expected, realCarlyle.getTitles());
    }
}
