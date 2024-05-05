package com.example.backendraspberrypiserver.test;

import com.fazecast.jSerialComm.SerialPort;

import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;

public class SerialCommunication {

    private SerialPort serialPort;

    public SerialCommunication(SerialPort serialPort) {
        this.serialPort = serialPort;
    }

//    public static void main(String[] args) {
//        SerialCommunication communication = new SerialCommunication();
//        communication.initializeSerialPort();
//        communication.startCommunication();
//    }

    public void initializeSerialPort() {
        serialPort.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 3200, 0);

        if (serialPort.openPort()) {
            System.out.println("Serial port opened successfully.");
        } else {
            System.out.println("Failed to open serial port.");
        }
    }

    public void startCommunication() {
        Thread readThread = new Thread(this::readData);
        Thread writeThread = new Thread(this::writeData);

        readThread.start();
        //writeThread.start();
    }

    private void readData() {
        try (InputStream in = serialPort.getInputStream()) {
            byte[] buffer = new byte[1024];
            int  len;
            while (true) {
                if (in.available() > 0) {
                    len = in.read(buffer);
                    String received = new String(buffer, 0, len);
                    System.out.println("time: " + LocalDateTime.now() + ", Received: " + received);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeData() {
        try (OutputStream out = serialPort.getOutputStream()) {
            String data = "Hello Arduino!";
            out.write(data.getBytes());
            out.flush();
            System.out.println("Data sent: " + data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

