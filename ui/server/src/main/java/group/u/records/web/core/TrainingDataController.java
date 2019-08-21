package group.u.records.web.core;

import group.u.records.ds.training.TrainingData;
import group.u.records.repository.TrainingDataRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/training")
public class TrainingDataController {

    private TrainingDataRepository trainingDataRepository;

    public TrainingDataController(TrainingDataRepository trainingDataRepository ){
        this.trainingDataRepository = trainingDataRepository;
    }
    @GetMapping
    public ResponseEntity<Page<TrainingData>> getAll(@RequestParam(value = "cursor", required = false, defaultValue="0") int cursor) {
        return ok(trainingDataRepository.findAll(PageRequest.of(cursor,100)));
    }
}
