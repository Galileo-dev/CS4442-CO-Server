package com.jj.server;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ClientTest {

    static Server server;
    static ExecutorService executorService;
    static Scanner scanner;
    int port = 8080;
    String host = "localhost";

    @BeforeClass
    public static void setUp(){
        // server = Server.getInstance();
        // executorService = Executors.newFixedThreadPool(10);
        // executorService.submit(()->{
        //     server.startServer();
        // });
        // scanner = new Scanner(System.in);
    }

    @AfterClass
    public static void tearDown(){
        // server.closeServerSocket();
        // executorService.shutdown();
        // scanner.close();
    }

    @Test
    public void testClientMessaging() throws UnknownHostException, IOException, InterruptedException{
        // Thread.sleep(1000);
        // int numberOfClients = 2;
        // Client[] clients = new Client[numberOfClients];
        // for (int i = 0; i < clients.length; i++) {
        //     String username = ""+(i+1);
        //     Socket socket = new Socket(host, port);
        //     clients[i] = new Client(socket, username, scanner);
        //     clients[i].listenForMessage();
        // }
        // for (int i = 0; i < clients.length; i++) {
        //     String[] msgs = {"Hello", "Hola", "Bonjour", "Merhaba", "Hallo"};
        //     for(String msg : msgs) {
        //         clients[i].sendMessage(msg);
        //     }
        // }

        // for (int i = 0; i < clients.length; i++) {
        //     clients[i].closeEverything();
        // }
    }
}