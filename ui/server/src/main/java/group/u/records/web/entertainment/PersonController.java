package group.u.records.web.entertainment;

import group.u.records.models.entity.Person;
import group.u.records.service.ActorService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/celebrity")
public class PersonController {
    private ActorService actorService;

    public PersonController(ActorService actorService){
        this.actorService = actorService;
    }

    @GetMapping
    public ResponseEntity<Page<Person>> search(@RequestParam("search") String searchString,
                                               @RequestParam(value = "quantity", required = false, defaultValue = "10") int quantity,
                                               @RequestParam(value = "cursor", required = false, defaultValue="0") int cursor){
        return ok(actorService.getActors(searchString, quantity, cursor));
    }
}
