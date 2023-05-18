package com.jj.server;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ClientTest {

    public static Server server;
    public static ExecutorService executorService;

    @BeforeClass
    public static void setUp(){
        server = Server.getInstance();
        server.startServer();
        executorService = Executors.newFixedThreadPool(10);
    }

    @AfterClass
    public static void tearDown(){
        server.closeServerSocket();
        executorService.shutdown();
    }

    @Test
    public void testClientMessaging(){
        int port = 8080;
        String host = "localhost";
        
        for (int i = 1; i <= 100; i++) {
            try (Socket socket = new Socket(host, port)) {
                String username = ""+i;
                Client client = new Client(socket, username);
                client.listenForMessage();
                client.sendMessage(username);

                String[] msgs = {"Hello", "Hola", "Bonjour", "Merhaba", "Hallo"};
                for(String msg : msgs){
                    client.sendMessage(msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
           }
    }
}
