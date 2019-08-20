package group.u.records.service;

import group.u.records.models.entity.Person;
import group.u.records.repository.MoviePublicSummaryRepository;
import group.u.records.repository.PersonRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.stream.Collectors;

@Service
public class ActorService {
    private PersonRepository personRepository;
    private MoviePublicSummaryRepository moviePublicSummaryRepository;

    public ActorService(PersonRepository personRepository, MoviePublicSummaryRepository moviePublicSummaryRepository) {
        this.personRepository = personRepository;
        this.moviePublicSummaryRepository = moviePublicSummaryRepository;
    }

    public Page<Person> getActors(@RequestParam("search") String searchString,
                                  @RequestParam(value = "quantity", required = false, defaultValue = "5") int quantity,
                                  @RequestParam(value = "cursor", required = false, defaultValue = "0") int cursor) {
        PageRequest pageRequest = PageRequest.of(cursor, quantity);

        Page<Person> byActorNameOrActorAliasOrTitleName = personRepository.findByActorNameOrActorAliasOrTitleName(searchString, pageRequest);
        byActorNameOrActorAliasOrTitleName.getContent().forEach(actor ->
                actor.getTitles().forEach(movieTitle ->
                        moviePublicSummaryRepository.findById(movieTitle.getId())
                            .ifPresent(moviePublicSummary ->
                                    movieTitle.setDossierAvailable(moviePublicSummary.isDossierAvailable()))));

        return byActorNameOrActorAliasOrTitleName;
    }
}
