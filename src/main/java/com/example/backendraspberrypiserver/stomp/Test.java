package com.example.backendraspberrypiserver.stomp;

import com.example.backendraspberrypiserver.serial.application.dto.ArduinoPowerData;
import com.example.backendraspberrypiserver.stomp.dto.PowerDataToCentralServerList;
import com.example.backendraspberrypiserver.stomp.dto.PowerDataToCentralServer;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;

public class Test {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        StompHandler stompHandler = new StompHandler();
        List<PowerDataToCentralServer> powerDataList = new ArrayList<>();
        PowerDataToCentralServerList myPowerDataList = new PowerDataToCentralServerList();

        ArduinoPowerData powerData1 = new ArduinoPowerData();
        powerData1.setPortNum(1L);
        powerData1.setPower(1.1);

        ArduinoPowerData powerData2 = new ArduinoPowerData();
        powerData2.setPortNum(2L);
        powerData2.setPower(2.2);

        ArduinoPowerData powerData3 = new ArduinoPowerData();
        powerData3.setPortNum(3L);
        powerData3.setPower(3.3);

        ArduinoPowerData powerData4 = new ArduinoPowerData();
        powerData4.setPortNum(4L);
        powerData4.setPower(4.4);

        ArduinoPowerData powerData5 = new ArduinoPowerData();
        powerData5.setPortNum(5L);
        powerData5.setPower(5.5);



        LocalDateTime now = LocalDateTime.now();
        PowerDataToCentralServer powerDataToCentralServer1 = convertForSendData(now, powerData1);
        PowerDataToCentralServer powerDataToCentralServer2 = convertForSendData(now, powerData2);
        PowerDataToCentralServer powerDataToCentralServer3 = convertForSendData(now, powerData3);
        PowerDataToCentralServer powerDataToCentralServer4 = convertForSendData(now, powerData4);
        PowerDataToCentralServer powerDataToCentralServer5 = convertForSendData(now, powerData5);

        powerDataToCentralServer1.setTime(now.toString());
        powerDataToCentralServer2.setTime(now.toString());
        powerDataToCentralServer3.setTime(now.toString());
        powerDataToCentralServer4.setTime(now.toString());
        powerDataToCentralServer5.setTime(now.toString());

        powerDataList.add(powerDataToCentralServer1);
        powerDataList.add(powerDataToCentralServer2);
        powerDataList.add(powerDataToCentralServer3);
        powerDataList.add(powerDataToCentralServer4);
        powerDataList.add(powerDataToCentralServer5);

        myPowerDataList.setPowerDataList(powerDataList);

        while(true){
            powerDataToCentralServer1.setPower(getRandomValue());
            powerDataToCentralServer2.setPower(getRandomValue());
            powerDataToCentralServer3.setPower(getRandomValue());
            powerDataToCentralServer4.setPower(getRandomValue());
            powerDataToCentralServer5.setPower(getRandomValue());

            stompHandler.sendData(myPowerDataList);
            Thread.sleep(1000);
        }
    }

    private static PowerDataToCentralServer convertForSendData(LocalDateTime now, ArduinoPowerData arduinoPowerData){
        return PowerDataToCentralServer.builder()
                .power(arduinoPowerData.getPower())
                .portId(arduinoPowerData.getPortNum())
                .powerSupplier(arduinoPowerData.getPowerSupplier())
                .time(now.toString())
                .build();
    }

    private static double getRandomValue(){
        return ThreadLocalRandom.current().nextDouble(5, 100);
    }
}
