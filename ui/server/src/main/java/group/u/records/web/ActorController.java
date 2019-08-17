package group.u.records.web;

import group.u.records.models.Actor;
import group.u.records.service.ActorService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/actors")
public class ActorController {
    private ActorService actorService;

    public ActorController(ActorService actorService ){
        this.actorService = actorService;
    }

    @GetMapping
    public ResponseEntity<Page<Actor>> search(@RequestParam("search") String searchString,
                                              @RequestParam(value = "cursor", required = false, defaultValue="0") int cursor){
        return ok(actorService.getActors(searchString, 10, cursor));
    }
}
