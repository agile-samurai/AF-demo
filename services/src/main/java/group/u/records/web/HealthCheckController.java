package group.u.records.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthCheckController {
    public HealthCheckController() {
    }

    @GetMapping
    public String checkHealth() {
        return "ok";
    }
}
