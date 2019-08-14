package group.u.records.web;

import group.u.records.service.EntertainmentDetailsService;
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

    public AdminController(EntertainmentDetailsService detailsService ){
        this.detailsService = detailsService;
    }

    @GetMapping("/stuff")
    public ResponseEntity<String> speak(){
        return ok( "Done" );
    }

    @PostMapping("/ingest")
    public ResponseEntity ingest(){
        detailsService.loadMovieDetails();

        return ok().build();
    }
}
