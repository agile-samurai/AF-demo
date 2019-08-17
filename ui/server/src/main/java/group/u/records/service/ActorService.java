package group.u.records.service;

import group.u.records.models.entity.Actor;
import group.u.records.repository.ActorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

@Service
public class ActorService {
    private Sort sort = new Sort(Sort.Direction.ASC, "name");
    private ActorRepository actorRepository;

    public ActorService(ActorRepository actorRepository) {
        this.actorRepository = actorRepository;
    }

    public Page<Actor> getActors(@RequestParam("search") String searchString,
                                 @RequestParam(value = "quantity", required = false, defaultValue = "10") int quantity,
                                 @RequestParam(value = "cursor", required = false, defaultValue = "0") int cursor) {
        PageRequest pageRequest = PageRequest.of(cursor, quantity, sort);

        return actorRepository.findByActorNameOrActorAliasOrTitleName(searchString, pageRequest);
    }
}
