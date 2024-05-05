package com.example.backendraspberrypiserver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/health")
@Slf4j
public class TestController {
    @GetMapping("")
    public String apiHealthTest() {
        return "rasberry !";

    }
}
