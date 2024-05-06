package com.example.backendraspberrypiserver.serial.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class PowerData {
    LocalDateTime time;
    Long portId;
    Double power;
    String powerSupplier;
}
