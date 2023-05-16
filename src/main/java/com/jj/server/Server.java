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
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                    System.exit(1);
                }
                // System.out.println("port: " + port);
            }
        }

        boolean inRunning = true;

        ServerSocket serverSocket = null;
        try {
            logger.info("Starting server...");
            serverSocket = new ServerSocket(port);

            logger.info("Server started on " + serverSocket.getInetAddress().getHostAddress() + ":"
                    + serverSocket.getLocalPort());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            System.exit(1);
        }

        while (inRunning) {
            try {
                Socket socket = serverSocket.accept();
                logger.info("Client connected from " + socket.getInetAddress().getHostAddress() + ":"
                        + socket.getPort());

                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                String helpMessage = "Type 'quit' to exit\n" + "Type 'help' for help\n";

                bufferedWriter.write("Welcome to the server!\n" + helpMessage);
                bufferedWriter.flush();

                boolean isConnected = true;
                while (isConnected) {
                    String line = bufferedReader.readLine();

                    switch (line) {
                        case "quit":
                            bufferedWriter.write("Goodbye :-(\n");
                            isConnected = false;
                            socket.close();
                            break;

                        case "help":
                            bufferedWriter.write(helpMessage);
                            bufferedWriter.flush();
                            break;

                        default:
                            bufferedWriter.write(line + "\n");
                            bufferedWriter.flush();
                    }
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                System.exit(1);
            }
        }

    }
}
