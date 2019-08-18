package group.u.records.people;

import group.u.records.models.Person;
import group.u.records.models.entity.MovieDetail;
import group.u.records.models.entity.MovieTitle;
import group.u.records.repository.PersonRepository;
import group.u.records.service.LevenshteinDistanceService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Component
public class PersonRegistry {

    private PersonRepository personRepository;
    private LevenshteinDistanceService levenshteinDistanceService;
    private Map<UUID, Person> personCache;

    public PersonRegistry(PersonRepository personRepository,
                          LevenshteinDistanceService levenshteinDistanceService) {
        this.personRepository = personRepository;
        this.levenshteinDistanceService = levenshteinDistanceService;
        this.personCache = new HashMap<>();
    }

    public void reconcile(Person person, MovieDetail movie) {
        Person existingMatchOrNull = personCache.values()
                .stream()
                .reduce(null, (accumulatedValue, personToCheckAgainst) -> {
                    String personName = person.getName().replace(".", "");
                    String personToCheckAgainstName = personToCheckAgainst.getName().replace(".", "");
                    boolean similarPersonExistsInCache = levenshteinDistanceService
                            .areStringsSufficientlySimilar(personName, personToCheckAgainstName);

                    if (similarPersonExistsInCache) {
                        return personToCheckAgainst;
                    }

                    return accumulatedValue;
                });

        if(existingMatchOrNull == null) {
            person.addTitle(MovieTitle.from(movie));
            personCache.put(person.getId(), person);
        } else {
            personCache.get(existingMatchOrNull.getId()).addTitle(MovieTitle.from(movie));
        }

        personRepository.save(person);
    }

    public void reconcile(List<MovieDetail> movieDetails) {
        movieDetails.forEach(m -> m.getPeople().forEach(p -> {
            p.addTitle(MovieTitle.from(m));
        }));
    }
}
