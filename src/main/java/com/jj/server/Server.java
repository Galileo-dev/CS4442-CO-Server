package com.jj.server;

import java.io.*;
import java.net.*;
import java.net.ServerSocket;
import java.util.logging.Logger;

// Implements ideas from: https://www.youtube.com/watch?v=gchR3DpY-8Q
public class Server {

    private static Server server = null;
    private static ServerSocket serverSocket = null;
    public static int port = 8080;
    private static Logger logger = Logger.getLogger(Server.class.getName());

    private Server() {

    }

    public static synchronized Server getInstance() {
        if (server == null) {
            server = new Server();
        }
        return server;
    }

    public static synchronized ServerSocket getServerSocket() throws IOException {
        if (serverSocket == null) {
            serverSocket = new ServerSocket(port);
        }
        return serverSocket;
    }

    public void startServer() {

        ClientHandler clientHandler;

        try {
            logger.info("Starting server...");
            // Get the singleton instance of the ServerSocket

            serverSocket = Server.getServerSocket();
            logger.info("Server socket created successfully.");

            logger.info("Server started on " + serverSocket.getInetAddress().getHostAddress() + ":"
                    + serverSocket.getLocalPort());
        } catch (IOException e) {
            logger.severe("Error starting the server: " + e.getMessage());
            System.exit(1);
        }

        while (!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                logger.info("Client connected from " + socket.getInetAddress().getHostAddress() + ":"
                        + socket.getPort());
                clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler);
                thread.start();

            } catch (

            Exception e) {
                logger.severe("Error connecting client: " + e.getMessage());

            }
        }
    }

    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException io) {
            logger.severe("Error stopping server:" + io.getMessage());
            io.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {

        if (args.length > 0 && args[0].equals("--help")) {
            String help = "Usage: java Server.jar --port=<port>\n" +
                    "Example: java Server.jar --port=8080\n" +
                    "         --port=<port> The port for the server to listen on";

            logger.info(help);
            System.exit(1);
        }

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

        server = Server.getInstance();
        server.startServer();

        try {
            logger.info("Stopping server...");
            server.closeServerSocket();
            logger.info("Server stopped");
        } catch (Exception e) {
            logger.severe("Error stopping server:" + e.getMessage());
            System.exit(1);

        }

    }

}