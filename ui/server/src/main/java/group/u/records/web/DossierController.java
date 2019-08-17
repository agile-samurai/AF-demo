package group.u.records.web;

import group.u.records.content.Dossier;
import group.u.records.security.DossierRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@RestController
@RequestMapping("/dossier")
public class DossierController {
    private DossierRepository dossierRepository;

    public DossierController(DossierRepository dossierRepository) {
        this.dossierRepository = dossierRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dossier> get(@PathVariable UUID id ){
        return ok(dossierRepository.get(id));
    }

    @PostMapping("/{id}/note")
    public ResponseEntity<Dossier> post(@PathVariable UUID id, @RequestBody String note ){
        dossierRepository.addNote(id, getContext().getAuthentication().getName(), note);
        return ok(dossierRepository.get(id));
    }

    @DeleteMapping("/{id}")
    @Secured("ROLE_SUPERVISOR")
    public ResponseEntity delete(@PathVariable UUID id){
        dossierRepository.delete(id);
        return ok().build();
    }


}
