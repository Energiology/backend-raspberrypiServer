package com.example.backendraspberrypiserver.stomp;

import com.example.backendraspberrypiserver.serial.application.dto.ArduinoPowerData;
import com.example.backendraspberrypiserver.serial.application.dto.PowerData;
import com.example.backendraspberrypiserver.stomp.dto.PowerDataToCentralServerList;
import com.example.backendraspberrypiserver.stomp.dto.PowerDataToCentralServer;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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

        PowerDataToCentralServer powerDataToCentralServer1 = mapper.map(powerData1, PowerDataToCentralServer.class);
        PowerDataToCentralServer powerDataToCentralServer2 = mapper.map(powerData2, PowerDataToCentralServer.class);
        LocalDateTime now = LocalDateTime.now();

        powerDataToCentralServer1.setTime(now.toString());
        powerDataToCentralServer2.setTime(now.toString());

        powerDataList.add(powerDataToCentralServer1);
        powerDataList.add(powerDataToCentralServer2);

        myPowerDataList.setPowerDataList(powerDataList);
        stompHandler.sendData(myPowerDataList);
    }
}
