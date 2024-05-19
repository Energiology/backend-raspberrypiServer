package com.example.backendraspberrypiserver.battery.presentaiton.dto.request;


import java.util.List;

public record BatterySwitchRequest(List<PortAndSupplier> portAndSuppliers) {

}


