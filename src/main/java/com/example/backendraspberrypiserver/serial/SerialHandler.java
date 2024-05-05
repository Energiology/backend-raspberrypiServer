package com.example.backendraspberrypiserver.serial;

import com.fazecast.jSerialComm.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
@Slf4j
public class SerialHandler {

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

        SerialPort serialPort = commPorts[1];

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
                    }
                }
            } catch (IOException e) {
                log.info("readSerial method end..");

                throw new RuntimeException(e);
            }
        }

        log.info("readSerial method end..");
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