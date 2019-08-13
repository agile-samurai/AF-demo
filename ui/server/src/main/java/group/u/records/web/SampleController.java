package group.u.records.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/hello")
public class SampleController {

    @GetMapping("/stuff")
    public ResponseEntity<String> speak(){
        return ok( "Done" );
    }

}
