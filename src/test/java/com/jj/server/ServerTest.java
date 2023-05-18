package com.jj.server;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;

/**
 * Unit test for simple App.
 */

class ServerThread extends Thread {
    public void run() {
        try {
            String[] args = {};
            Server.main(args);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

public class ServerTest {
    /**
     * Rigorous Test :-)
     */

    private static Thread serverThread;

    @BeforeClass
    public static void setUp() throws Exception {
        // start the server
        serverThread = new ServerThread();
        serverThread.start();

        // check that the server is running before we can run tests
        int maxAttempts = 10;
        int attempts = 0;
        while (attempts < maxAttempts) {
            try {
                Socket clientSocket = new Socket("localhost", 8080);
                clientSocket.close();
                break;
            } catch (ConnectException e) {
                attempts++;
                Thread.sleep(100);
            }
        }
        if (attempts == maxAttempts) {
            throw new Exception("Server failed to start");
        }

    }

    @AfterClass
    public static void tearDown() throws Exception {
        serverThread.interrupt();
    }

    @Test
    public void serverShouldAllowConnection() throws UnknownHostException,
            IOException {
        Socket clientSocket = new Socket("localhost", 8080);
        assertTrue(clientSocket.isConnected());
        clientSocket.close();
    }

    @Test
    public void serverShouldAllowMultipleConnections() throws UnknownHostException, IOException {
        Socket clientSocket1 = new Socket("localhost", 8080);
        assertTrue(clientSocket1.isConnected());
        Socket clientSocket2 = new Socket("localhost", 8080);
        assertTrue(clientSocket2.isConnected());
        clientSocket1.close();
        clientSocket2.close();
    }

    @Test
    public void serverShouldGiveStartMessage() throws UnknownHostException,
            IOException {
        Socket clientSocket = new Socket("localhost", 8080);
        assertTrue(clientSocket.isConnected());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

        // send username
        bufferedWriter.write("username\n");
        bufferedWriter.flush();

        // read start message
        String startMessage = bufferedReader.readLine();

        assertTrue(startMessage.equals("Welcome username!"));
        bufferedReader.close();
        bufferedWriter.close();
        clientSocket.close();
    }
}