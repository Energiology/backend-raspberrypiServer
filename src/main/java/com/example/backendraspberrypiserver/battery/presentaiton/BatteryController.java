package com.example.backendraspberrypiserver.battery.presentaiton;

import com.example.backendraspberrypiserver.battery.application.BatteryControllerService;
import com.example.backendraspberrypiserver.battery.presentaiton.dto.request.BatterySwitchRequest;
import com.example.backendraspberrypiserver.battery.presentaiton.dto.response.BatterySwitchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ports")
public class BatteryController {
    private final BatteryControllerService batteryControllerService;
    @PostMapping("/battery-switch")
    public ResponseEntity<BatterySwitchResponse> batterySwitch(@RequestBody BatterySwitchRequest batterySwitchRequest){
        BatterySwitchResponse batterySwitchResponse = batteryControllerService.switchPowerSupplier(batterySwitchRequest);

        return ResponseEntity.ok(
                batterySwitchResponse
        );
    }
}