package com.example.backendraspberrypiserver.stomp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class StompHandler {
    private StompSession stompSession;
    public final String WEB_SOCKET_URL = "ws://localhost:8080/raspberrypi-websocket";

    StompSession connect(final String webSocketURL) throws ExecutionException, InterruptedException {
        // WebSocket 연결을 위한 WebSocketClient 생성
        StandardWebSocketClient webSocketClient = new StandardWebSocketClient();

        // WebSocketTransport 사용하여 Transport 리스트 생성
        Transport webSocketTransport = new WebSocketTransport(webSocketClient);
        List<Transport> transports = Collections.singletonList(webSocketTransport);

        // SockJsClient에 Transport 리스트 설정
        SockJsClient sockJsClient = new SockJsClient(transports);

        // WebSocketStompClient 생성
        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        String url = "ws://localhost:8080/raspberrypi-websocket";
        StompSession session = stompClient.connect(url, new StompSessionHandlerAdapter() {}).get();
        this.stompSession = session;
        return session;
    }

    public void sendData(Object data) throws ExecutionException, InterruptedException {
        if (this.stompSession == null || !stompSession.isConnected())
            this.stompSession = connect(WEB_SOCKET_URL);

        this.stompSession.send("/main-server/data", data);
    }
}
