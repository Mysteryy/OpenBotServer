package org.consumer;

import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialFactory;
import org.server.ClientHandler;
import org.server.ReceiveThread;

/**
 * Created by zach on 9/11/2015.
 */
public class ConsumerThread extends Thread {
    private ReceiveThread receiveThread;
    private ClientHandler clientHandler;
    private boolean shouldRun = true;

    private Serial serial;

    public ConsumerThread(ReceiveThread receiveThread, ClientHandler clientHandler) {
        this.receiveThread = receiveThread;
        this.clientHandler = clientHandler;

//        this.serial = SerialFactory.createInstance();
//        serial.open(Serial.DEFAULT_COM_PORT, 38400);
    }

    @Override
    public void run() {
        while(shouldRun){
            if(receiveThread.hasMessage()){
                processMessage();
            }
        }
        System.out.println("Consumer Thread Stopped");
    }

    private void processMessage(){
        String message = receiveThread.getMessage();
        if(message.contains(":")){
            String[] splitMessage = message.split(":");
            if(splitMessage.length == 2){
                String command = splitMessage[0];
                String jointNumber = splitMessage[1];
                System.out.println("Command Received | Command: " + command + " | Joint Number: " + jointNumber);
                if(command.equalsIgnoreCase("Get")){
                    clientHandler.sendMessage("Pos:123");
                }
            }
            else if(splitMessage.length == 3){
                String command = splitMessage[0];
                String jointNumber = splitMessage[1];
                String angle = splitMessage[2];
                System.out.println("Command: " + command + " | Joint Number: " + jointNumber + " | Angle: " + angle);
            }
        }
    }

    public void kill(){
        this.shouldRun = false;
    }
}
