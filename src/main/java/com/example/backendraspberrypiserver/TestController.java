package com.example.backendraspberrypiserver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

public class TestController {
    @RestController
    @RequestMapping("api/health")
    @Slf4j
    public class HealthTestController {
        @GetMapping("")
        public String apiHealthTest() {
            return "rasberry !";
        }

    }
}
