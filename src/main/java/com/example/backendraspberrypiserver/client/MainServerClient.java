package com.example.backendraspberrypiserver.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name="main-server", url = "http://localhost:8080")
public interface MainServerClient {
    @PostMapping("/api/port/init/batterySupplier")
    void requestInitPortBattery();
}

