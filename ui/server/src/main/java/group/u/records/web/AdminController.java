package group.u.records.web;

import group.u.records.service.EntertainmentDetailsService;
import group.u.records.service.S3DataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/processing")
public class AdminController {

    private EntertainmentDetailsService detailsService;

    public AdminController(EntertainmentDetailsService detailsService){
        this.detailsService = detailsService;
    }

    @PostMapping("/ingest")
    public ResponseEntity ingest(){
        detailsService.loadMovieDetails();
        return ok().build();
    }
}
