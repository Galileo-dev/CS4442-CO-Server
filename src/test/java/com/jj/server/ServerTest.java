package com.jj.server;

import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
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
    private static Socket clientSocket;
    private static BufferedReader bufferedReader;
    private static BufferedWriter bufferedWriter;

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
                clientSocket = new Socket("localhost", 8080);
                bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
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
        bufferedReader.close();
        bufferedWriter.close();
        clientSocket.close();
        serverThread.interrupt();

    }

    @Test
    public void serverShouldAllowConnection() throws UnknownHostException,
            IOException {
        assertTrue(clientSocket.isConnected());
    }

    @Test
    public void serverStartMessage() throws UnknownHostException,
            IOException {

        // send username
        bufferedWriter.write("username\n");
        bufferedWriter.flush();

        // read start message
        String startMessage = bufferedReader.readLine();
        assertTrue(startMessage.equals("Welcome username!"));

    }

    @Test
    public void serverListCommand() throws UnknownHostException,
            IOException {
        // send username
        bufferedWriter.write("/list\n");
        bufferedWriter.flush();

        // read start message
        String listMessage = bufferedReader.readLine();
        assertTrue(listMessage.equals("1 users online"));
    }

    public void serverExitCommand() throws UnknownHostException,
            IOException {
        // send username
        bufferedWriter.write("/exit\n");
        bufferedWriter.flush();

        // read start message
        String listMessage = bufferedReader.readLine();
        assertTrue(!clientSocket.isClosed());
    }

}