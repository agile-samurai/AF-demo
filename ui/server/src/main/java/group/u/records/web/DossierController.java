package group.u.records.web;

import group.u.records.security.MasterDossierRepository;
import group.u.records.service.MasterDossier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@RestController
@RequestMapping("/dossier")
public class DossierController {
    private MasterDossierRepository masterDossierRepository;

    public DossierController(MasterDossierRepository masterDossierRepository) {
        this.masterDossierRepository = masterDossierRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<MasterDossier> get(@PathVariable UUID id ){
        return ok(masterDossierRepository.get(id));
    }

    @PostMapping("/{id}/note")
    public ResponseEntity<MasterDossier> post(@PathVariable UUID id, @RequestBody String note ){
        masterDossierRepository.addNote(id, getContext().getAuthentication().getName(), note);
        return ok(masterDossierRepository.get(id));
    }

    @DeleteMapping("/{id}")
    @Secured("ROLE_SUPERVISOR")
    public ResponseEntity delete(@PathVariable UUID id){
        masterDossierRepository.delete(id);
        return ok().build();
    }


}
