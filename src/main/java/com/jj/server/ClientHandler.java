package com.jj.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Logger;

// Implements ideas from: https://www.youtube.com/watch?v=gchR3DpY-8Q

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;
    private Logger logger = Logger.getLogger(ClientHandler.class.getName());

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // use the first message from the client as the username
            this.clientUsername = bufferedReader.readLine();
            if (this.clientUsername == null) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                Thread.currentThread().interrupt();
            }
            // introduce new user to the chat
            broadcastMessage("SERVER: " + clientUsername + " has entered the chat");
            addClientHandler();

            // only send welcome message to the new user
            this.bufferedWriter.write("Welcome " + clientUsername + "!");
            this.bufferedWriter.newLine();
            this.bufferedWriter.flush();
        } catch (IOException io) {
            logger.info("ClientHandler constructor: " + io.getMessage());

            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    @Override
    public void run() {
        // listen to separate messages
        String input;
        while (socket.isConnected() && !socket.isClosed() && bufferedWriter != null) {
            try {
                // get input from user
                input = bufferedReader.readLine();
                String commandString = "\n" +
                        "/list - list users online\n" +
                        "/help - list available commands\n" +
                        "/exit - exit chat\n";
                // determine what the user wants to do
                switch (input) {
                    // user types /exit close the socket
                    case "/exit":
                        bufferedWriter.write("Goodbye " + clientUsername + " ;)");
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                        socket.close();
                        break;
                    // user types /list show the number of connected users
                    case "/list":
                        bufferedWriter.write(clientHandlers.size() + " users online");
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                        break;
                    // user types /help show the available commands
                    case "/help":
                        bufferedWriter.write("available commands:" + commandString);
                        bufferedWriter.flush();
                        break;
                    default:
                        // user types a unkown command
                        if (input.startsWith("/")) {
                            bufferedWriter.write("command not found\n available commands:" + commandString);
                            bufferedWriter.flush();
                        } else {
                            // send to all clients like a standard group chat
                            broadcastMessage(clientUsername + ": " + input);
                        }
                        break;

                }
            } catch (Exception e) {
                logger.severe("Error processing client data: " + e.getMessage());
                closeEverything(socket, bufferedReader, bufferedWriter);

            }
        }
    }

    // synchronized to stop multiple threads from trying to send a message at the
    // same time
    // this can cause a race condition as sendMessage() could be called from
    // multiple
    // threads through broadcastMessage()
    public void sendMessage(String message) {
        try {
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException io) {
            logger.severe("Error sending message: " + io.getMessage());
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void broadcastMessage(String message) {
        // don't broadcast if the client has disconnected
        if (clientUsername != null) {
            // iterate over the shared list of client handlers
            for (ClientHandler clientHandler : clientHandlers) {
                try {
                    // make sure the handler has an active buffered writer and that it's not the
                    // current client
                    if (!clientHandler.equals(this)
                            && (clientHandler.bufferedWriter != null)) {
                        sendMessage(message);
                    }
                } catch (Exception io) {
                    logger.severe("Error broadcasting message: " + io.getMessage());
                    closeEverything(socket, bufferedReader, bufferedWriter);
                }

            }
        }
    }

    // synchronized to prevent race condition
    public synchronized void addClientHandler() {
        clientHandlers.add(this);
    }

    // synchronized to prevent race condition
    public synchronized void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessage("SERVER: " + clientUsername + " has left the chat");
    }

    // remove the client handler from the list and close everything
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        // remove the client handler from the list as it is disconnected
        removeClientHandler();
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
            logger.severe("Error closing everything: " + io.getMessage());
        }
    }
}
