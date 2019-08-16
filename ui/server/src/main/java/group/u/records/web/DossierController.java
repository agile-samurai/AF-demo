package group.u.records.web;

import group.u.records.content.Dossier;
import group.u.records.security.DossierRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.ResponseEntity.ok;

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
}
