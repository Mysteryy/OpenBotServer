package org.server;

import org.consumer.ConsumerThread;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by zach on 9/9/2015.
 */
public class ClientHandler {
    private Socket socket;
    private ReceiveThread receiveThread;
    private SendThread sendThread;
    private ConsumerThread consumerThread;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        receiveThread = new ReceiveThread(this, socket);
        sendThread = new SendThread(socket);
        consumerThread = new ConsumerThread(receiveThread, this);

        receiveThread.start();
        sendThread.start();
        consumerThread.start();
    }

    public void killClient(){
        System.out.println("Killing all threads...");
        receiveThread.kill();
        sendThread.kill();
        consumerThread.kill();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("All threads killed, socket closed, client disconnected.");
    }

    public void sendMessage(String message){
        this.sendThread.sendMessage(message);
    }

    public String getMessage(){
        return receiveThread.getMessage();
    }
}
