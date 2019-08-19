package group.u.records.web;

import group.u.records.models.web.NoteDTO;
import group.u.records.security.MasterDossierService;
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
    private MasterDossierService masterDossierService;

    public DossierController(MasterDossierService masterDossierService) {
        this.masterDossierService = masterDossierService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<MasterDossier> get(@PathVariable UUID id ){
        return ok(masterDossierService.get(id));
    }

    @PostMapping("/{id}/note")
    public ResponseEntity<MasterDossier> post(@PathVariable UUID id, @RequestBody NoteDTO note ){
        masterDossierService.addNote(id, getContext().getAuthentication().getName(), note.getContent());
        return ok(masterDossierService.get(id));
    }

    @DeleteMapping("/{id}")
    @Secured("ROLE_SUPERVISOR")
    public ResponseEntity delete(@PathVariable UUID id){
        masterDossierService.delete(id);
        return ok().build();
    }
}
