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

    // static Server server;
    // static ExecutorService executorService;
    // static Scanner scanner;
    // int port = 8080;
    // String host = "localhost";

    // @BeforeClass
    // public static void setUp(){
    //     server = Server.getInstance();
    //     server.startServer();
    //     executorService = Executors.newFixedThreadPool(10);
    //     scanner = new Scanner(System.in);
    // }

    // @AfterClass
    // public static void tearDown(){
    //     server.closeServerSocket();
    //     executorService.shutdown();
    //     scanner.close();
    // }

    // @Test
    // public void testClientMessaging() throws UnknownHostException, IOException{

    //     Socket socket = new Socket(host, port);
    //     for (int i = 1; i <= 1; i++) {
    //         String username = ""+i;
    //         Client client = new Client(socket, username, scanner);
    //         client.listenForMessage();

    //         client.sendMessage(username);
    //         String[] msgs = {"Hello", "Hola", "Bonjour", "Merhaba", "Hallo"};
    //         for(String msg : msgs) {
    //             client.sendMessage(msg);
    //         }
    //     } 
    // }
}
