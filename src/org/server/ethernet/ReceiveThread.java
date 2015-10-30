package org.server.ethernet;

import org.server.ClientHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by zach on 9/9/2015.
 * <p>
 * This class is responsible for listing for data over the socket.
 * If any data is received, the data will be added to the inbound queue.
 * This data can be processed at a later time by a consumer.
 * <p>
 * To send a message, create a Client object and use that object
 * to handle sending and receiving messages.
 */
public class ReceiveThread extends Thread {
    // Socket used to establish a connection with the server, passed in from the Client object
    private Socket socket;
    // The buffered reader is used to read data over the socket
    private BufferedReader bufferedReader;
    // boolean to keep the thread running
    private boolean shouldRun = true;
    // Any data received over the socket will be added to this queue, and can be processed when appropriate
    private ConcurrentLinkedQueue<String> inboundQueue = new ConcurrentLinkedQueue<String>();
    // The instance of the client handler
    private ClientHandler clientHandler;

    /**
     * Default constructor
     *
     * @param socket the socket being used for this connection.
     */
    public ReceiveThread(ClientHandler clientHandler, Socket socket) {
        // use the same socket as the client handler object
        this.socket = socket;
        // set the instance of client handler
        this.clientHandler = clientHandler;
        // Create a new buffered reader using the socket input stream
        try {
            this.bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Override the run method for the thread. This method will
     * run until ClientHandler#kill() is called.
     * <p>
     * The purpose of this run method is to listen for any data
     * being sent by a client, and add it to the inbound queue.
     */
    @Override
    public void run() {
        // Keep looping until shouldRun gets set to false
        while (this.shouldRun) {
            try {
                // Wait for some data
                String string = this.bufferedReader.readLine();
                // Print the data out (debug purposes)
                if (string != null) {
                    // Add the received data to the inbound queue
                    this.inboundQueue.add(string);
                } else {
                    this.clientHandler.killClient();
                }
            } catch (IOException e) {
                // Kill off the thread if an exception occurs
                try {
                    this.socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                this.shouldRun = false;
            }
        }
        // The thread has been killed if this line prints out
        System.out.println("Receive Thread Stopped");
    }

    /**
     * Checks if there is a message in the inbound queue. This method will
     * not remove the message from the queue if there is one.
     *
     * @return true if there is a message in the inbound queue, false if not.
     */
    public boolean hasMessage() {
        return !this.inboundQueue.isEmpty();
    }

    /**
     * Gets a message from the inbound queue, if one exist.
     *
     * @return the message that has been in the inbound queue the longest.
     * if the queue is empty, null is returned.
     */
    public String getMessage() {
        return this.inboundQueue.poll();
    }

    /**
     * Kills the thread (stops running the thread)
     * <p>
     * Do not call this method directly, instead kill the client using
     * Client#killClient(), which will handle killing all other threads.
     */
    public void kill() {
        this.shouldRun = false;
        this.interrupt();
    }
}