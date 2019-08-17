package group.u.records.web;

import group.u.records.models.Person;
import group.u.records.service.PersonService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/actors")
public class PersonController {
    private PersonService personService;

    public PersonController(PersonService personService){
        this.personService = personService;
    }

    @GetMapping
    public ResponseEntity<Page<Person>> search(@RequestParam("search") String searchString,
                                               @RequestParam(value = "quantity", required = false, defaultValue = "10") int quantity,
                                               @RequestParam(value = "cursor", required = false, defaultValue="0") int cursor){
        return ok(personService.getActors(searchString, quantity, cursor));
    }

}
