package org.server;

import org.server.serial.SerialHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by zach on 9/9/2015.
 *
 * The Server object is the main driver in this program. This object will listen for
 * new connections on the specified port, and spawn a new ClientHandler object
 * each time a new client connects.
 */
public class Server extends Thread{
    // Server socket object
    private ServerSocket serverSocket;
    // The port that is being used by this server
    private final int port = 38200;
    // The same SerialHandler object is shared among all clients that connect, since there is no need for multiple instances.
    private SerialHandler serialHandler;

    /**
     * Default constructor
     */
    public Server(){
        try {
            // create a new ServerSocket object
            this.serverSocket = new ServerSocket(this.port);
            // create a new SerialHandler object to handle all serial communication
            this.serialHandler = new SerialHandler();
            // print that the server has started
            System.out.println("Server Started");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        // Run until the server is killed
        while(true){
            try {
                // see if there are any new connections to accept
                Socket clientSocket = serverSocket.accept();
                // If there is a new connection
                if(clientSocket != null){
                    // Print out to inform the server
                    System.out.println("New Client Connnected: " + clientSocket.getInetAddress());
                    // spawn a new ClientHandler object to take care of the communication for this client
                    ClientHandler clientHandler = new ClientHandler(clientSocket, serialHandler);
                    // set the serial handler to use this newly established client, meaning this client will now receive the replies
                    serialHandler.setClientHandler(clientHandler);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
