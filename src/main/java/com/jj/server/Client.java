package com.jj.server;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.logging.Logger;

// Implements ideas from: https://www.youtube.com/watch?v=gchR3DpY-8Q
public class Client {

    private Socket socket;
    private String username;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
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

            try (Scanner scanner = new Scanner(System.in)) {
                while (socket.isConnected()) {
                    String messageToSend = scanner.nextLine();
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromChat;
                while (socket.isConnected() && !socket.isClosed()) {
                    try {
                        msgFromChat = bufferedReader.readLine();
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
        Logger logger = Logger.getLogger(Client.class.getName());

        int port = 8080;
        String host = "localhost";
        Socket socket = new Socket(host, port);

        try {
            logger.info("Connecting to server...");
            logger.info(
                    "Connected to server on " + socket.getInetAddress().getHostAddress() + ":" + socket.getLocalPort());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            System.exit(1);
        }

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            Client client = new Client(socket, username);
            client.listenForMessage();
            client.sendMessage(username);
            while (socket.isConnected() && !socket.isClosed()) {
                String input = scanner.nextLine();
                client.sendMessage(input);
            }
        }
    }
}
