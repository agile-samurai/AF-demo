package group.u.records.web;

import group.u.records.service.DataScienceAPIService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloWorldController {

    private DataScienceAPIService dataScienceAPIService;

    public HelloWorldController(DataScienceAPIService dataScienceAPIService) {
        this.dataScienceAPIService = dataScienceAPIService;
    }

    @GetMapping("/metrics")
    public String getMetrics() { return dataScienceAPIService.getMetrics(); }
}
