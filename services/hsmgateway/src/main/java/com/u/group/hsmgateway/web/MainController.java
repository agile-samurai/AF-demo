package com.u.group.hsmgateway.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class MainController {

    @RequestMapping("hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello");
    }
}
