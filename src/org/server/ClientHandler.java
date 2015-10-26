package org.server;

import org.consumer.ConsumerThread;
import org.server.ethernet.ReceiveThread;
import org.server.ethernet.SendThread;
import org.server.serial.SerialHandler;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by zach on 9/9/2015.
 */
public class ClientHandler {
    // Socket object, passed in from the Server object which creates a ClientHandler object for each client that connects.
    private Socket socket;
    // The receive thread is in charge of putting received data in to a inbound queue to be processed later
    private ReceiveThread receiveThread;
    // The send thread is in charge of putting messages in to an outbound queue to send to the client
    private SendThread sendThread;
    // The consumer thread consumes received data
    private ConsumerThread consumerThread;
    // The SerialHandler object is used to communicate with the Teensy 3.1 via serial
    private SerialHandler serialHandler;

    public ClientHandler(Socket socket, SerialHandler serialHandler) {
        // Set the socket instance, passed in from the Server object
        this.socket = socket;
        // Set the serial handler object, passed in from the Server object
        this.serialHandler = serialHandler;
        // Create the new threads
        this.receiveThread = new ReceiveThread(this, socket);
        this.sendThread = new SendThread(this, socket);
        this.consumerThread = new ConsumerThread(this);

        // Start the new threads
        this.receiveThread.start();
        this.sendThread.start();
        this.consumerThread.start();
    }

    /**
     * Kills the client and all threads within the client.
     * This method will also attempt to close the socket.
     */
    public void killClient() {
        System.out.println("Killing all threads...");
        // Call kill methods for all threads
        this.receiveThread.kill();
        this.sendThread.kill();
        this.consumerThread.kill();
        // Attempt to close the socket
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("All threads killed, client disconnected.");
    }

    /**
     * Checks if there is a message in the inbound queue. This method will
     * not remove the message from the queue if there is one.
     *
     * @return true if there is a message in the inbound queue, false if not.
     */
    public boolean hasMessage() {
        return this.receiveThread.hasMessage();
    }

    /**
     * Gets a message from the inbound queue, if one exist.
     *
     * @return the message that has been in the inbound queue the longest.
     * if the queue is empty, null is returned.
     */
    public String getMessage() {
        return this.receiveThread.getMessage();
    }

    /**
     * Adds a message to the outbound queue for this connection.
     * The messages are sent out over the socket in the order that they are added to the queue.
     *
     * @param message the message to add to the outbound queue
     */
    public void sendMessage(String message) {
        this.sendThread.sendMessage(message);
    }
}
