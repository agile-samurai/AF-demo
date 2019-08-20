package group.u.records.web.entertainment;

import group.u.records.models.MovieTitle;
import group.u.records.models.web.NoteDTO;
import group.u.records.repository.MoviePublicSummaryRepository;
import group.u.records.security.MasterDossierService;
import group.u.records.service.dossier.MasterDossier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@RestController
@RequestMapping("/dossier")
public class DossierController {
    private MasterDossierService masterDossierService;
    private MoviePublicSummaryRepository publicSummaryRepository;

    public DossierController(MasterDossierService masterDossierService,
                             MoviePublicSummaryRepository publicSummaryRepository) {
        this.masterDossierService = masterDossierService;
        this.publicSummaryRepository = publicSummaryRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<MasterDossier> get(@PathVariable UUID id ){
        return ok(enrichWithSimilarities(id));
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

    private MasterDossier enrichWithSimilarities(@PathVariable UUID id) {
        MasterDossier masterDossier = masterDossierService.get(id);
        List<MovieTitle> movieTitles = masterDossier.getSimilarMovies()
                .stream()
                .map(s -> (publicSummaryRepository.findById(s)))
                .filter( s -> s.isPresent() )
                .map(s->MovieTitle.from(s.get()))
                .collect(Collectors.toList());

        masterDossier.setSimilarMovieTitles(movieTitles);
        return masterDossier;
    }
}
