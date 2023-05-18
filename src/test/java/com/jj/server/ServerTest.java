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

    @Test
    public void serverConverse() throws UnknownHostException, IOException {
        // create a new client first
        Socket clientSocket2 = new Socket("localhost", 8080);
        // give them a namme
        BufferedWriter bufferedWriter2 = new BufferedWriter(new OutputStreamWriter(clientSocket2.getOutputStream()));
        BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(clientSocket2.getInputStream()));
        // clear the reader

        bufferedWriter2.write("username2\n");
        bufferedWriter2.flush();

        // client 1 reads the user join message
        String userJoinMessage = bufferedReader.readLine();
        assertTrue(userJoinMessage.equals("SERVER: username2 has entered the chat"));

        // read welcome message
        String welcomeMessage = bufferedReader2.readLine();
        System.out.println(welcomeMessage);
        assertTrue(welcomeMessage.equals("Welcome username2!"));

        // now let's get them to talk
        bufferedWriter.write("hello\n");
        bufferedWriter.flush();

        // client 2 reads the message
        String message1 = bufferedReader2.readLine();
        System.out.println(message1);
        assertTrue(message1.equals("username: hello"));

        // client 2 responds
        bufferedWriter2.write("hi\n");
        bufferedWriter2.flush();

        String message2 = bufferedReader.readLine();
        assertTrue(message2.equals("username2: hi"));

        bufferedReader2.close();
        bufferedWriter2.close();
        clientSocket2.close();
    }

    @Test
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