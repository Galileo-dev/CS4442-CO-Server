package com.jj.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Logger;

// https://www.youtube.com/watch?v=gLfuZrrfKes&t=359s
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

            // get the username
            this.clientUsername = bufferedReader.readLine();
            if (this.clientUsername == null) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                Thread.currentThread().interrupt();
            }

            addClientHandler();
            broadcastMessage("SERVER: " + clientUsername + " has entered the chat");
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

        while (socket.isConnected()) {
            try {
                input = bufferedReader.readLine();

                switch (input) {
                    case "/exit":
                        bufferedWriter.write("Goodbye " + clientUsername + " ;)");
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                        socket.close();
                        break;
                    case "/list":
                        bufferedWriter.write(clientHandlers.size() + " users online");
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                        break;
                    default:
                        broadcastMessage(clientUsername + ": " + input);
                        break;
                }

            } catch (IOException io) {
                logger.severe("Error processing client data: " + io.getMessage());
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    public void broadcastMessage(String message) {
        if (clientUsername != null) {
            for (ClientHandler clientHandler : clientHandlers) {
                try {
                    if (!clientHandler.clientUsername.equals(clientUsername)) {
                        if (clientHandler.bufferedWriter != null) {
                            clientHandler.bufferedWriter.write(message);
                            clientHandler.bufferedWriter.newLine();
                            clientHandler.bufferedWriter.flush();
                        }
                    }
                } catch (IOException io) {
                    logger.severe("Error broadcasting message: " + io.getMessage());
                    closeEverything(socket, bufferedReader, bufferedWriter);
                }
            }
        }
    }

    public synchronized void addClientHandler() {
        clientHandlers.add(this);
    }

    public synchronized void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessage("SERVER: " + clientUsername + " has left the chat");
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
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
