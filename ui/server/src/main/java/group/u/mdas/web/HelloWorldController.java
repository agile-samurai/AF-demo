package group.u.mdas.web;

import group.u.mdas.service.DataScienceAPIService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloWorldController {

    private DataScienceAPIService dataScienceAPIService;

    public HelloWorldController(DataScienceAPIService dataScienceAPIService) {
        this.dataScienceAPIService = dataScienceAPIService;
    }

    @GetMapping
    public String getHelloWorld() {
        return dataScienceAPIService.getHelloWorldScore();
    }

    @GetMapping("/metrics")
    public String getMetrics() { return dataScienceAPIService.getMetrics(); }
}
