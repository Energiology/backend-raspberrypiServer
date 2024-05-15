package com.example.backendraspberrypiserver.serial;

import com.example.backendraspberrypiserver.serial.application.dto.ArduinoPowerData;
import com.example.backendraspberrypiserver.stomp.StompHandler;
import com.example.backendraspberrypiserver.stomp.dto.PowerDataToCentralServer;
import com.example.backendraspberrypiserver.stomp.dto.PowerDataToCentralServerList;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
@Slf4j
public class SerialHandler {
    private final ObjectMapper objectMapper;
    private String currentPowerSupplier = "battery";

    @Scheduled(fixedDelay = 2000)
    public void readSerial() throws ExecutionException, InterruptedException, IOException {
        String[] args = {};

//        Test.main(args);
        StompHandler stompHandler = new StompHandler();

        SerialPort[] commPorts = SerialPort.getCommPorts();

        if (commPorts.length < 1) {
            log.error("System has no Serial Port");
            return;
        }

        SerialPort serialPort = commPorts[0];
        setSerialPortProperties(serialPort);
        setSerialPortEventListener(serialPort);

        if (serialPort.openPort()) {
            System.out.println("Serial port connection successful");
            // Get the input stream
            InputStream inputStream = serialPort.getInputStream();
            OutputStream outputStream = serialPort.getOutputStream();

            try {
                int data;
                int length;
                while (SerialPort.getCommPorts().length > 0 && serialPort.isOpen()) {
                    if (inputStream.available() > 0) {
                        log.info("connect!");

                        byte[] buffer = new byte[2048];
                        length = inputStream.read(buffer);

                        String receivedString = new String(buffer, 0, length, StandardCharsets.UTF_8); // 바이트를 문자열로 변환
                        log.info("\n" + "time :" + LocalDateTime.now() + "\n" + "Received data: \n" + receivedString);

                        PowerDataToCentralServerList powerDataToCentralServerList = convertToSendData(receivedString);
                        stompHandler.sendData(powerDataToCentralServerList);
                    }
                }
            } catch (IOException e) {
                serialPort.closePort();
                log.info("readSerial method end..");
                log.info(e.getLocalizedMessage());
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
            serialPort.closePort();
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
            serialPort.closePort();
                throw new RuntimeException(e);
            }
            finally {
                serialPort.closePort();
            }

        }else{
            log.info("port is closed!");
        }

        log.info("readSerial method end..");
    }

    //    @Scheduled(fixedDelay = 2000)
    public void readSerial2() {
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

        SerialPort serialPort = commPorts[0];

        setSerialPortProperties(serialPort);
        setSerialPortEventListener(serialPort);

        if (serialPort.openPort()) {
            System.out.println("Serial port connection successful");

            // Get the input stream
            InputStream inputStream = serialPort.getInputStream();
            OutputStream outputStream = serialPort.getOutputStream();

            try {
                int data;
                int length;
                while (SerialPort.getCommPorts().length > 0 && serialPort.isOpen()) {
                    if (inputStream.available() > 0) {
                        byte[] buffer = new byte[2048];
                        length = inputStream.read(buffer);

                        String receivedString = new String(buffer, 0, length, StandardCharsets.UTF_8); // 바이트를 문자열로 변환
                        log.info("\n" + "time: " + LocalDateTime.now() + "\n" + "Received data: \n" + receivedString);

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
}