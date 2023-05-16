package com.jj.server;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.net.*;

/**
 * Unit test for simple App.
 */

class ServerThread extends Thread {
    public void run() {
        String[] args = {};
        Server.main(args);
    }
}

public class ServerTest {
    /**
     * Rigorous Test :-)
     */

    Thread serverThread;
    Socket clientSocket;

    @Before
    public void setUp() throws Exception {
        // start the server
        serverThread = new ServerThread();
        serverThread.start();
        // connect to the server
        Thread.sleep(1000);
        clientSocket = new Socket("localhost", 8080);
    }

    @After
    public void tearDown() throws Exception {

        serverThread.interrupt();
    }

    @Test
    public void shouldServer() {
        assertTrue(true);
    }
}
