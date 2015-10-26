package org.server.ethernet;

import org.server.ClientHandler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by zach on 9/9/2015.
 * <p>
 * This class is responsible for sending any messages that get added to
 * the outbound queue. Sending messages is done by adding messages
 * to the outbound queue from a producer, and then this object
 * will send the data as appropriate.
 */
public class SendThread extends Thread {
    // Socket used to establish a connection with the server, passed in from the ClientHandler object
    private Socket socket;
    // The buffered writer is used to write data over the socket
    private BufferedWriter bufferedWriter;
    // Thread safe queue used to send messages. Any messages added to this queue will be sent by this thread.
    private ConcurrentLinkedQueue<String> outboundQueue = new ConcurrentLinkedQueue<String>();
    // boolean to keep the thread running
    private boolean shouldRun = true;
    // The instance of the client handler
    private ClientHandler clientHandler;

    /**
     * Default constructor
     *
     * @param socket the socket object passed in from the Client object
     */
    public SendThread(ClientHandler clientHandler, Socket socket) {
        // Set the socket instance
        this.socket = socket;
        // Set the client handler instance
        this.clientHandler = clientHandler;
        // try to create a new buffered writer using the socket output stream
        try {
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Override the run method for the thread. This method will
     * run until ClientHandler#kill() is called.
     * <p>
     * The purpose of this run method is to send any messages that
     * are in the outbound queue.
     */
    @Override
    public void run() {
        // Run until the thread is killed
        while (this.shouldRun) {
            // Check the queue to see if there is anything that needs to be sent
            String stringToSend = this.outboundQueue.poll();
            // If there is something to send
            if (stringToSend != null) {
                // Write the message over the socket
                this.writeMessage(stringToSend);
            }
        }
        // The thread has been killed if this line prints out, no more messages can be sent.
        System.out.println("Send Thread Stopped");
    }

    /**
     * This is the method that should be called to send a message over the socket.
     * This method will add data to the outbound queue, to be sent over the socket.
     *
     * @param message the message to send over the socket.
     */
    public void sendMessage(String message) {
        // Add the message to the outbound queue
        this.outboundQueue.add(message);
    }

    /**
     * Kills the thread (stops running the thread)
     * <p>
     * Do not call this method directly, instead kill the client handler using
     * ClientHandler#killClient(), which will handle killing all other threads.
     */
    public void kill() {
        this.shouldRun = false;
        this.interrupt();
    }

    /**
     * This method will send each data element in the outbound queue
     * over the socket using a buffered writer. When a new element is
     * added to the outbound queue, it will eventually be polled and sent
     * using this method.
     *
     * @param message
     */
    private void writeMessage(String message) {
        // Convert the string to a char array
        char[] charArray = message.toCharArray();
        try {
            if (!this.socket.isClosed() && this.socket.isConnected()) {
                // Send the char array over the socket
                this.bufferedWriter.write(charArray);
                // Write a new line after each message to signal the end of the message when reading on the server.
                this.bufferedWriter.newLine();
                // and of course, flush the buffer
                this.bufferedWriter.flush();
            }
        } catch (IOException e) {
            this.clientHandler.killClient();
        }
    }
}
