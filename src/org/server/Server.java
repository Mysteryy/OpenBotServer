package org.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by zach on 9/9/2015.
 */
public class Server extends Thread{
    private ServerSocket serverSocket;
    private final int port = 38200;

    public Server(){
        try {
            this.serverSocket = new ServerSocket(port);
            System.out.println("Server Started");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        while(true){
            try {
                Socket clientSocket = serverSocket.accept();
                if(clientSocket != null){
                    System.out.println("New Client Connnected: " + clientSocket.getInetAddress());
                    ClientHandler clientHandler = new ClientHandler(clientSocket);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
