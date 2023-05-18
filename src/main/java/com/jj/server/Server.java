package com.jj.server;

import java.io.*;
import java.net.*;
import java.util.logging.Logger;

public class Server {

    public static void main(String[] args) {
        Logger logger = Logger.getLogger(Server.class.getName());

        if (args.length > 0 && args[0].equals("--help")) {
            System.out.println("Usage: java Server.jar --port=<port>");
            System.out.println("Example: java Server.jar --port=8080");
            System.out.println("        --port=<port> The port for the server to listen on");
            System.exit(1);
        }

        int port = 8080;

        for (String arg : args) {
            if (arg.startsWith("--port=")) {
                try {
                    port = Integer.parseInt(arg.substring(7));
                } catch (NumberFormatException e) {
                    logger.severe("Invalid port number: " + e.getMessage());
                    System.exit(1);
                }
            }
        }

        boolean inRunning = true;

        ServerSocket serverSocket = null;
        try {
            logger.info("Starting server...");
            serverSocket = new ServerSocket(port);

            logger.info("Server started on " + serverSocket.getInetAddress().getHostAddress() + ":"
                    + serverSocket.getLocalPort());
        } catch (IOException e) {
            logger.severe("Error starting the server: " + e.getMessage());
            System.exit(1);
        }

        while (inRunning) {
            try {
                Socket socket = serverSocket.accept();
                logger.info("Client connected from " + socket.getInetAddress().getHostAddress() + ":"
                        + socket.getPort());

                ClientHandler clientHandler = new ClientHandler(socket);

                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            } catch (IOException e) {
                logger.severe("Error connecting client: " + e.getMessage());
                System.exit(1);
            }
        }
        try {
            logger.info("Stopping server...");
            serverSocket.close();
            logger.info("Server stopped");
        } catch (IOException e) {
            logger.severe("Error stopping server:" + e.getMessage());
            System.exit(1);

        }

    }
}
