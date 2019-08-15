package group.u.records.service;

import group.u.records.models.Actor;
import group.u.records.repository.ActorRepository;
import org.elasticsearch.common.unit.Fuzziness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

@Service
public class ActorService {
//    private Sort sort = new Sort(Sort.Direction.ASC, "name");
    private ActorRepository actorRepository;

    public ActorService(ActorRepository actorRepository) {
        this.actorRepository = actorRepository;
    }

    public Page<Actor> getActors(@RequestParam("search") String searchString,
                                 @RequestParam(value = "quantity", required = false, defaultValue = "10") int quantity,
                                 @RequestParam(value = "cursor", required = false, defaultValue = "0") int cursor) {
        PageRequest pageRequest = PageRequest.of(cursor, quantity);

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchQuery("name", searchString)
                        .fuzziness(Fuzziness.ONE))
                .withPageable(pageRequest)
                .build();

        return actorRepository.search(searchQuery);
    }
}
