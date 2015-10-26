package org.consumer;

import org.server.ClientHandler;

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
    // boolean to keep the thread running
    private boolean shouldRun = true;
    // The instance of the UI

    /**
     * Default constructor
     *
     * @param clientHandler the instance of Client that is being used for communication
     */
    public ConsumerThread(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
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
        String message = clientHandler.getMessage();
        if (message.contains(":")) {
            String[] splitMessage = message.split(":");
            if (splitMessage.length == 2) {
                String command = splitMessage[0];
                String jointNumber = splitMessage[1];
                System.out.println("Command Received | Command: " + command + " | Joint Number: " + jointNumber);
                if (command.equalsIgnoreCase("Get")) {
                    clientHandler.sendMessage("Pos:123");
                }
            } else if (splitMessage.length == 3) {
                String command = splitMessage[0];
                String jointNumber = splitMessage[1];
                String angle = splitMessage[2];
                System.out.println("Command: " + command + " | Joint Number: " + jointNumber + " | Angle: " + angle);
            }
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
