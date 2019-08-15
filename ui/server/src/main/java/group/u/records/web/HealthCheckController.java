package group.u.records.web;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/health")
public class HealthCheckController {

    Logger logger = LoggerFactory.getLogger(HealthCheckController.class);

    @GetMapping
    public ResponseEntity<String> getStatus(){
        logger.debug( "Health checked at:  " + LocalDateTime.now());
        return ok("Healthy");
    }
}
