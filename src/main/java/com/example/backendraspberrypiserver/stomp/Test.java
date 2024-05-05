package com.example.backendraspberrypiserver.stomp;

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

        PowerData powerData1 = new PowerData();
        powerData1.setPortId(1L);
        powerData1.setPower(1.1);

        PowerData powerData2 = new PowerData();
        powerData2.setPortId(2L);
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
