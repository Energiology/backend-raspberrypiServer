package com.example.backendraspberrypiserver.stomp.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class PowerDataToCentralServer {
    String time;
    Long portId;
    Double power;
    String powerSupplier;
}
