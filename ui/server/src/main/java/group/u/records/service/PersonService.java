package group.u.records.service;

import group.u.records.models.Person;
import group.u.records.repository.PersonRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

@Service
public class PersonService {
    private Sort sort = new Sort(Sort.Direction.ASC, "name");
    private PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public Page<Person> getActors(@RequestParam("search") String searchString,
                                  @RequestParam(value = "quantity", required = false, defaultValue = "10") int quantity,
                                  @RequestParam(value = "cursor", required = false, defaultValue = "0") int cursor) {
        PageRequest pageRequest = PageRequest.of(cursor, quantity, sort);

        return personRepository.findByActorNameOrActorAliasOrTitleName(searchString, pageRequest);
    }
}
