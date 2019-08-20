package group.u.records.repository.people;

import group.u.records.models.entity.Person;
import group.u.records.models.MovieDetail;
import group.u.records.models.MovieTitle;
import group.u.records.repository.PersonRepository;
import group.u.records.datascience.LevenshteinDistanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private Logger logger = LoggerFactory.getLogger(PersonRegistry.class);

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
            mergeAliases(movie, person);
            logger.debug("Saving existing person:  " + person );
            personRepository.save(person);
        } else {
            Person person1 = personCache.get(existingMatchOrNull.getId());
            person1.addTitle(MovieTitle.from(movie));

            mergeAliases(movie, person1);
            logger.debug("Saving updated person:  " + person );
            personRepository.save(person1);
        }
    }

    private void mergeAliases(MovieDetail movie, Person person) {
        logger.debug("About to merge characters. " + person.getName() );
        logger.debug("Movie value:  " + movie );
        logger.debug("Character count:  " + movie.getCharacters().size());
        movie.getCharacters().stream().forEach(m->{
            Person personFromCharacter = m.toPerson();
            person.mergeIfPossible(personFromCharacter);
        });
    }

    public void reconcile(List<MovieDetail> movieDetails) {
        movieDetails.forEach(m -> m.getPeople().forEach(p -> {
            p.addTitle(MovieTitle.from(m));
        }));
    }
}
