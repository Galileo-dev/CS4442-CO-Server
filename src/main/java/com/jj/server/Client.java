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
    private Scanner scanner;
    public static Logger logger = Logger.getLogger(Client.class.getName());

    //constructor initializes the instance variables, sets up the input and output streams for the socket and sends the username to the server
    public Client(Socket socket, String username, Scanner scanner) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.scanner = scanner;
            sendMessage(username);
        } catch (IOException io) {
            closeEverything();
        }
    }

    //sends messages to the server by writing it to the output stream and flushing it
    public void sendMessage(String message) throws IOException {
        if (!socket.isConnected()) return;
        bufferedWriter.write(message);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    //thread is created to listen for messages from the server
    //reads lines from input stream and prints them to the console if they're not null
    //runs until socket is closed or disconnected
    public void listenForMessage() {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                String msgFromChat;
                // ensure socket is connected and not closed
                while (socket.isConnected() && !socket.isClosed()) {
                    try {
                        Thread.sleep(100);
                        msgFromChat = bufferedReader.readLine();
                        if (msgFromChat != null) {
                            System.out.println(msgFromChat);

                        }

                    } catch (IOException | InterruptedException e) {
                        closeEverything();
                    }
                }
            }
        });
        thread.start();
    }

    // helper method to close all things related to the socket
    //closes: input and output streams, socket and scanner
    private void closeEverything() {

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
            if (scanner != null) {
                scanner.close();
            }
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        int port = 8080;
        String host = "localhost";
        Socket socket = new Socket(host, port);
        Scanner scanner = new Scanner(System.in);

        logger.info("Connecting to server...");
        logger.info("Connected to server on "
                    + socket.getInetAddress().getHostAddress() 
                    + ":" 
                    + socket.getLocalPort());

        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        Client client = new Client(socket, username, scanner);
        client.listenForMessage();

        String messageToSend;
        while (socket.isConnected() && !socket.isClosed()) {
            messageToSend = scanner.nextLine();
            if (!messageToSend.isBlank())
                client.sendMessage(messageToSend);

        }
        client.closeEverything();
    }
}

