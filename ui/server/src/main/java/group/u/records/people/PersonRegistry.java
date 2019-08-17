package group.u.records.people;

import group.u.records.models.Person;
import group.u.records.models.entity.MovieDetail;
import group.u.records.models.entity.MovieTitle;
import group.u.records.repository.PersonRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Component
public class PersonRegistry {

    private PersonRepository personRepository;
    private Map<UUID, Person> personCache;

    public PersonRegistry(PersonRepository personRepository){
        this.personRepository = personRepository;
        this.personCache = new HashMap();
    }

    public void reconcile(Person person, MovieDetail movie) {
        Person workingPerson = person;
        if( personCache.containsKey(person.getId()))
            workingPerson = personCache.get(person.getId());

        workingPerson.addTitle(MovieTitle.from(movie));
        personRepository.save(person);
    }

    public void reconcile(List<MovieDetail> movieDetails) {
        movieDetails.forEach(m-> m.getPeople().forEach(p-> {
            p.addTitle(MovieTitle.from(m));
        }));
    }
}
