package com.example.backendraspberrypiserver.battery.presentaiton.dto.response;

import java.util.List;

public record BatterySwitchResponse(
        List<PortAndResult> portAndResults
) {
}
