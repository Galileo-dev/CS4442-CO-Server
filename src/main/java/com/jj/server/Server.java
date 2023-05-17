package com.jj.server;

import java.io.*;
import java.net.*;
import java.util.logging.Logger;

// Implements ideas from: https://www.youtube.com/watch?v=gchR3DpY-8Q
public class Server {

    private ServerSocket serverSocket;
    public static int port = 8080;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        Logger logger = Logger.getLogger(Server.class.getName());

        try {
            logger.info("Starting server...");
            serverSocket = new ServerSocket(port);

            logger.info("Server started on " + serverSocket.getInetAddress().getHostAddress() + ":"
                    + serverSocket.getLocalPort());

            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                logger.info("Client connected from " + socket.getInetAddress().getHostAddress() + ":"
                        + socket.getPort());
                ClientHandler clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {

        if (args.length > 0 && args[0].equals("--help")) {
            System.out.println("Usage: java Server.jar --port=<port>");
            System.out.println("Example: java Server.jar --port=8080");
            System.out.println("        --port=<port> The port for the server to listen on");
            System.exit(1);
        }

        for (String arg : args) {
            if (arg.startsWith("--port=")) {
                try {
                    port = Integer.parseInt(arg.substring(7));
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                    System.exit(1);
                }
                // System.out.println("port: " + port);
            }
        }

        ServerSocket serverSocket = new ServerSocket(port);
        Server server = new Server(serverSocket);
        server.startServer();
    }
}
