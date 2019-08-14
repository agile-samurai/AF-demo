package group.u.records.web;

import group.u.records.models.Actor;
import group.u.records.repository.ActorRepository;
import org.elasticsearch.common.unit.Fuzziness;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/actors")
public class ActorController {
    private ActorRepository actorRepository;

    public ActorController(ActorRepository actorRepository ){
        this.actorRepository = actorRepository;
    }

    @GetMapping
    public ResponseEntity<Page<Actor>> search(@RequestParam("search") String searchString ){
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchQuery("fullName", searchString)
                        .fuzziness(Fuzziness.TWO))
                .build();

        return ok(actorRepository.search(searchQuery));
    }

    @GetMapping("/all")
    public ResponseEntity<Iterable<Actor>> all(){
        return ok(actorRepository.findAll());
    }

}
