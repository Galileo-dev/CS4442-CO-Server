package com.jj.server;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.logging.Logger;

// Implements ideas from: https://www.youtube.com/watch?v=gchR3DpY-8Q
public class Client {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
    private Logger logger = Logger.getLogger(Client.class.getName());

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = username;
        } catch (IOException io) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendMessage(String message) {
        try {
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            // initialize scanner to read from terminal
            try (Scanner scanner = new Scanner(System.in)) {
                while (socket.isConnected()) {
                    // get input from user
                    String messageToSend = scanner.nextLine();
                    // send input to the server
                    bufferedWriter.write(messageToSend);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }

            }
        } catch (IOException io) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void listenForMessage() {
        // run on separate thread.
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromChat;
                // ensure socket is connected and not closed
                while (socket.isConnected() && !socket.isClosed()) {
                    try {

                        msgFromChat = bufferedReader.readLine();

                        // msgFromChat will be null when the bufferedReader is closed
                        if (msgFromChat == null) {
                            closeEverything(socket, bufferedReader, bufferedWriter);
                        }
                        System.out.println(msgFromChat);
                    } catch (IOException io) {
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

    // helper method to close all things related to the socket
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        // logging setup
        Logger logger = Logger.getLogger(Client.class.getName());

        int port = 8080;
        String host = "localhost";
        Socket socket = null;

        // attempt to connect to server
        try {
            logger.info("Connecting to server...");
            socket = new Socket(host, port);
            logger.info(
                    "Connected to server on " + socket.getInetAddress().getHostAddress() + ":" + socket.getLocalPort());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            // exit with error code 1
            System.exit(1);
        }

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter username: ");
            String username = scanner.nextLine();

            // create client object
            Client client = new Client(socket, username);
            client.listenForMessage();
            // first message sent to server is used as the username
            client.sendMessage(username);

            // read from terminal and send to server
            while (socket.isConnected() && !socket.isClosed()) {
                String input = scanner.nextLine();
                client.sendMessage(input);
            }
        }
    }
}
