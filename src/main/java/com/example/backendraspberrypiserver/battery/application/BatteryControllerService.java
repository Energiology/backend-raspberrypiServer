package com.example.backendraspberrypiserver.battery.application;

import com.example.backendraspberrypiserver.battery.presentaiton.dto.request.BatterySwitchRequest;
import com.example.backendraspberrypiserver.battery.presentaiton.dto.request.PortAndSupplier;
import com.example.backendraspberrypiserver.battery.presentaiton.dto.response.BatterySwitchResponse;
import com.example.backendraspberrypiserver.battery.presentaiton.dto.response.PortAndResult;
import com.example.backendraspberrypiserver.serial.SerialHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BatteryControllerService {
    private final SerialHandler serialHandler;

    public BatterySwitchResponse switchPowerSupplier(BatterySwitchRequest batterySwitchRequest){
        List<PortAndSupplier> portAndSuppliers = batterySwitchRequest.portAndSuppliers();

        for (PortAndSupplier portAndSupplier : portAndSuppliers) {
            Long portId = portAndSupplier.portId();
            String powerSupplier = portAndSupplier.powerSupplier();

            serialHandler.requestToArduino(portId, powerSupplier);
        }

        return generateBatterySwitchResponse(portAndSuppliers);
    }

    private BatterySwitchResponse generateBatterySwitchResponse(List<PortAndSupplier> portAndSuppliers){
        List<PortAndResult> portAndResults = portAndSuppliers.stream().map(
                (e) -> {
                    return new PortAndResult(e.portId(), "success");
                }
        ).toList();

        return new BatterySwitchResponse(portAndResults);
    }
}
