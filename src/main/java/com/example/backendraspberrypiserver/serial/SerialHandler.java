package com.example.backendraspberrypiserver.serial;

import com.example.backendraspberrypiserver.serial.application.dto.ArduinoPowerData;
import com.example.backendraspberrypiserver.serial.application.dto.PowerData;
import com.example.backendraspberrypiserver.stomp.StompHandler;
import com.example.backendraspberrypiserver.stomp.dto.PowerDataToCentralServer;
import com.example.backendraspberrypiserver.stomp.dto.PowerDataToCentralServerList;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fazecast.jSerialComm.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
@Slf4j
public class SerialHandler {
    private final StompHandler stompHandler;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 2000)
    public void readSerial() {
        log.info("readSerial method Start..");
        SerialPort[] commPorts = SerialPort.getCommPorts();

        for (SerialPort port : commPorts) {
            System.out.println("Serial port found: " + port.getSystemPortName());
        }


        if (commPorts.length < 1) {
            log.error("System has no Serial Port");
            return;
        }

        SerialPort serialPort = commPorts[0];

        int baudRate = 9600;
        int dataBits = 8;
        int stopBits = SerialPort.ONE_STOP_BIT;
        int parity = SerialPort.NO_PARITY;

        serialPort.setComPortParameters(baudRate, dataBits, stopBits, parity);
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 500, 0);

        if (serialPort.openPort()) {
            System.out.println("Serial port connection successful");

            // Get the input stream
            InputStream inputStream = serialPort.getInputStream();
            OutputStream outputStream = serialPort.getOutputStream();

            try {
                int data;
                int length;
                while (SerialPort.getCommPorts().length > 0 && serialPort.isOpen()) {
//                  log.info("before read..");
                    if (inputStream.available() > 0) {
                        byte[] buffer = new byte[2048];
                        length = inputStream.read(buffer);

//                      log.info("after read..");
                        String receivedString = new String(buffer, 0, length, StandardCharsets.UTF_8); // 바이트를 문자열로 변환
                        log.info("time :" + LocalDateTime.now() + " Received data: " + receivedString);

                        PowerDataToCentralServerList powerDataToCentralServerList = convertToSendData(receivedString);
                        stompHandler.sendData(powerDataToCentralServerList);
                    }
                }
            } catch (IOException e) {
                log.info("readSerial method end..");
                log.info(e.getLocalizedMessage());
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        log.info("readSerial method end..");
    }

    private PowerDataToCentralServerList convertToSendData(String longJson) throws JsonProcessingException {
        String[] jsons = longJson.split("\n");
        LocalDateTime now = LocalDateTime.now();
        List<PowerDataToCentralServer> powerDataList = new ArrayList<>();
        PowerDataToCentralServerList powerDataToCentralServerList = new PowerDataToCentralServerList();

        for (String json : jsons) {
            ArduinoPowerData arduinoPowerData = objectMapper.readValue(json, ArduinoPowerData.class);

            PowerDataToCentralServer powerData = PowerDataToCentralServer.builder()
                    .power(arduinoPowerData.getPower())
                    .powerSupplier(arduinoPowerData.getPowerSupplier())
                    .portId(arduinoPowerData.getPortNum())
                    .time(now.toString())
                    .build();

            powerDataList.add(powerData);
        }

        powerDataToCentralServerList.setPowerDataList(powerDataList);
        return powerDataToCentralServerList;
    }


//    @Scheduled(fixedDelay = 100000000)
//    public void readSerial() {
//        log.info("readSerial method Start..");
//        SerialPort[] commPorts = SerialPort.getCommPorts();
//
//        for (SerialPort port : commPorts) {
//            System.out.println("Serial port found: " + port.getSystemPortName());
//        }
//
//
////        if (commPorts.length <= 1) {
////            log.error("System has no Serial Port");
////            return;
////        }
//
//        SerialPort serialPort = SerialPort.getCommPort("COM4");
//
//        SerialCommunication serialCommunication = new SerialCommunication(serialPort);
//        serialCommunication.initializeSerialPort();
//        serialCommunication.startCommunication();
//
//        log.info("readSerial method end..");
//    }
}