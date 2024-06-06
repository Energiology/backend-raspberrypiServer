package com.example.backendraspberrypiserver.serial;

import com.example.backendraspberrypiserver.client.MainServerClient;
import com.example.backendraspberrypiserver.serial.application.dto.ArduinoPowerData;
import com.example.backendraspberrypiserver.stomp.StompHandler;
import com.example.backendraspberrypiserver.stomp.dto.PowerDataToCentralServer;
import com.example.backendraspberrypiserver.stomp.dto.PowerDataToCentralServerList;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
@Slf4j
public class SerialHandler {
    private final ObjectMapper objectMapper;
    private String currentPowerSupplier = "battery";
    private SerialPort serialPort1;
    private SerialPort serialPort2;
    private Map<String, Integer> map;
    List<List<String>> lookUpCommand;

    private final MainServerClient mainServerClient;

    @PostConstruct
    void init(){
         map = new HashMap<String, Integer>() {{
            put("BATTERY", 0);
            put("EXTERNAL", 1);
            put("OFF", 2);
        }};

        lookUpCommand = new ArrayList<>();
        lookUpCommand.add(List.of("tmp"));
        lookUpCommand.add(List.of("a","b", "c"));
        lookUpCommand.add(List.of("d","e", "f"));
        lookUpCommand.add(List.of("g","h", "i"));
        lookUpCommand.add(List.of("j","k", "l"));
        lookUpCommand.add(List.of("m","n", "o"));
    }

    @Scheduled(fixedDelay = 2000)
    public void readSerial() throws IOException {
        String[] args = {};

        StompHandler stompHandler = new StompHandler();

        SerialPort[] commPorts = SerialPort.getCommPorts();

        if (commPorts.length < 1) {
            log.error("System has no Serial Port");
            return;
        }


        serialPort1 = commPorts[0];
        setSerialPortProperties(serialPort1);
        setSerialPortEventListener(serialPort1);

        if (serialPort1.openPort()) {
            System.out.println("Serial port connection successful");
            mainServerClient.requestInitPortBattery();
            // Get the input stream
            InputStream inputStream = serialPort1.getInputStream();

            int length;
            while (SerialPort.getCommPorts().length > 0 && serialPort1.isOpen()) {
                if (inputStream.available() > 0) {

                    byte[] buffer = new byte[2048];
                    length = inputStream.read(buffer);

                    String receivedString = new String(buffer, 0, length, StandardCharsets.UTF_8); // 바이트를 문자열로 변환
                    log.info("\n" + "time :" + LocalDateTime.now() + "\n" + "Received data: \n" + receivedString);

                    PowerDataToCentralServerList powerDataToCentralServerList = null;
                    try{
                         powerDataToCentralServerList = convertToSendData(receivedString);
                    } catch (JsonProcessingException e){
                        log.error(e.toString());
                    }

                    try {
                        stompHandler.sendData(powerDataToCentralServerList);
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }else{
            log.info("port is closed!");
        }

        log.info("readSerial method end..");
    }

    @Scheduled(fixedDelay = 2000)
    public void readSerial2() throws IOException {
        StompHandler stompHandler = new StompHandler();

        log.info("readSerial2222 method Start..");
        SerialPort[] commPorts = SerialPort.getCommPorts();

        for (SerialPort port : commPorts) {
            System.out.println("Serial port found: " + port.getSystemPortName());
        }


        if (commPorts.length < 1) {
            log.error("System has no Serial Port");
            return;
        }

        serialPort2 = commPorts[1];
        setSerialPortProperties(serialPort2);
        setSerialPortEventListener(serialPort2);

        if (serialPort2.openPort()) {
            System.out.println("Serial port connection successful");

            // Get the input stream
            InputStream inputStream = serialPort2.getInputStream();
            int length;
            while (SerialPort.getCommPorts().length > 0 && serialPort2.isOpen()) {
                if (inputStream.available() > 0) {

                    byte[] buffer = new byte[2048];
                    length = inputStream.read(buffer);

                    String receivedString = new String(buffer, 0, length, StandardCharsets.UTF_8); // 바이트를 문자열로 변환
                    log.info("\n" + "time :" + LocalDateTime.now() + "\n" + "Received data: \n" + receivedString);

                    PowerDataToCentralServerList powerDataToCentralServerList = null;
                    try{
                        powerDataToCentralServerList = convertToSendData(receivedString);
                    } catch (JsonProcessingException e){
                        log.error(e.toString());
                    }

                    try {
                        stompHandler.sendData(powerDataToCentralServerList);
                    } catch (ExecutionException e) {
                        serialPort2.closePort();
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        serialPort2.closePort();
                        throw new RuntimeException(e);
                    }
                }
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
                    .powerSupplier(currentPowerSupplier)
                    .build();

            powerDataList.add(powerData);
        }

        powerDataToCentralServerList.setPowerDataList(powerDataList);
        return powerDataToCentralServerList;
    }

    private static void setSerialPortProperties(SerialPort serialPort) {
        int baudRate = 9600;
        int dataBits = 8;
        int stopBits = SerialPort.ONE_STOP_BIT;
        int parity = SerialPort.NO_PARITY;

        serialPort.setComPortParameters(baudRate, dataBits, stopBits, parity);
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 500, 0);
    }

    private static void setSerialPortEventListener(SerialPort serialPort) {
        serialPort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return  SerialPort.LISTENING_EVENT_PORT_DISCONNECTED;
            }

            @Override
            public void serialEvent(SerialPortEvent event) {
                if (event.getEventType() == SerialPort.LISTENING_EVENT_PORT_DISCONNECTED) {
                    serialPort.closePort();
                    System.out.println("Serial port disconnected.");
                }
            }
        });
    }

    public String requestToArduino(Long portId,  String command){
        SerialPort serialPort = serialPort1;
        String commandCode = mapToCommandCode(portId, command);

        if (serialPort.isOpen() || serialPort.openPort()) {
            try {
                String fullCommand = commandCode;
                char charCode = commandCode.charAt(0);
                byte[] commandBytes = new byte[] {(byte) charCode};

                serialPort.writeBytes(commandBytes, commandBytes.length);
                log.info("Command sent to Arduino: " + fullCommand);

                return "Command sent successfully";
            } catch (Exception e) {
                log.error("Error sending command to Arduino", e);
                return "Error sending command: " + e.getMessage();
            }
        } else {
            String errorMessage = "Failed to open serial port";
            log.error(errorMessage);
            return errorMessage;
        }
    }

    private String mapToCommandCode(Long portId, String command){
        Integer idx = map.get(command);
        return lookUpCommand.get(portId.intValue()).get(idx);
    }

}