package com.example.backendraspberrypiserver.stomp.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PowerDataToCentralServer {
    String time;
    Long portId;
    Double power;
    String powerSupplier;
}
