package com.itcast;

import com.itcast.server.WebSocketServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RedbookNettyApplication {

    private static final WebSocketServer webSocketServer = new WebSocketServer();

    public static void main(String[] args) {
        SpringApplication.run(RedbookNettyApplication.class, args);
        webSocketServer.run();
    }

}
