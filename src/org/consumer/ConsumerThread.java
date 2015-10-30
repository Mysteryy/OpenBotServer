package org.consumer;

import org.server.ClientHandler;
import org.server.serial.SerialHandler;

/**
 * Created by zach on 9/11/2015.
 * <p>
 * The consumer thread is where any data consumption should take place.
 * This thread should handle removing any data from the inbound queue
 * and using it as necessary.
 */
public class ConsumerThread extends Thread {
    // The receiveThread is where the inbound queue is, get data from here.
    private ClientHandler clientHandler;
    // the instance of serial handler
    private SerialHandler serialHandler;
    // boolean to keep the thread running
    private boolean shouldRun = true;
    // The instance of the UI

    /**
     * Default constructor
     *
     * @param clientHandler the instance of Client that is being used for communication
     */
    public ConsumerThread(ClientHandler clientHandler, SerialHandler serialHandler) {
        this.clientHandler = clientHandler;
        this.serialHandler = serialHandler;
    }

    /**
     * Override the run method for this thread.
     * <p>
     * This method should not be changed. This method called
     * processMessage, that is where any message processing should
     * take place.
     */
    @Override
    public void run() {
        while (this.shouldRun) {
            // Check if there are any messages in the clients inbound queue
            if (this.clientHandler.hasMessage()) {
                // Call processMessage() to handle the new message
                this.processMessage();
            }
        }
        // If this prints out, the thread has been killed
        System.out.println("Consumer Thread Stopped");
    }

    /**
     * Any message processing should take place in here.
     * Remove messages from the inbound queue here, and use
     * them as appropriate.
     */
    private void processMessage() {
        // if the client handler has a message, then it should be sent to the teensy over serial
        String messageToTeensy = this.clientHandler.getMessage();
        // if the serial handler has a message, then it should be sent to the client over socket
        String messageToClient = this.serialHandler.getMessage();

        // if there is a message to send to the teensy
        if(messageToTeensy != null){
            // send the message using the serial handler
            System.out.println("To teensy: " + messageToTeensy);
            serialHandler.sendMessage(messageToTeensy);
        }
        // if there is a message to send to the client
        if(messageToClient != null){
            // send the message using the client handler
            System.out.println("To client: " + messageToClient);
            clientHandler.sendMessage(messageToClient);
        }
    }

    /**
     * Kills the thread (stops running the thread)
     * <p>
     * Do not call this method directly, instead kill the client using
     * Client#killClient(), which will handle killing all other threads.
     */
    public void kill() {
        this.shouldRun = false;
    }

    private int parseInt(String text) {
        int parsedInt = -1;
        try {
            parsedInt = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            System.out.println("Could not convert to int: " + text);
        }
        return parsedInt;
    }
}
