package org.server.serial;

import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataListener;
import com.pi4j.io.serial.SerialFactory;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by zach on 10/25/2015.
 */
public class SerialHandler {
    // Instance of the serial object (pi4j library)
    private Serial serial;
    // Any data received over serial be added to this queue, and can be processed when appropriate
    private ConcurrentLinkedQueue<String> inboundQueue = new ConcurrentLinkedQueue<String>();

    /**
     * Default constructor
     */
    public SerialHandler(){
        // Create a new serial object
        serial = SerialFactory.createInstance();
        // Specify the port to open, and the baud rate to use
        serial.open("/dev/ttyS1", 115200);
        // Add a listener to receive serial data
        serial.addListener(new SerialDataListener() {
            @Override
            public void dataReceived(SerialDataEvent serialDataEvent) {
                // add the new message to the inbound queue
                inboundQueue.add(serialDataEvent.getData());
                System.out.print(serialDataEvent.getData());
            }
        });
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
     * Sends a message via serial communication.
     *
     * @param message the message to send out over serial
     */
    public void sendMessage(String message) {
        this.serial.writeln(message);
    }
}
