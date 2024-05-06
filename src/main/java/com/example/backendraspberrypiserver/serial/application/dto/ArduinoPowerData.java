package com.example.backendraspberrypiserver.serial.application.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArduinoPowerData {
    private Long arduinoId;
    private String type;
    private Long  portNum;
    private String powerSupplier;
    private Double power;
}
