package org.server;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.PriorityQueue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by zach on 9/9/2015.
 */
public class ReceiveThread extends Thread {
    private Socket socket;
    private BufferedReader bufferedReader;
    private boolean shouldRun = true;
    private ConcurrentLinkedQueue<String> receivedData = new ConcurrentLinkedQueue<String>();
    private ClientHandler clientHandler;

    public ReceiveThread(ClientHandler clientHandler, Socket socket) {
        this.socket = socket;
        this.clientHandler = clientHandler;

        try {
            this.bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        while(shouldRun){
            try {
                String string = this.bufferedReader.readLine();
                if(string != null){
                    this.receivedData.add(string);
                }
                else if(string == null){
                    clientHandler.killClient();
                }
            } catch (IOException e) {
                try {
                    bufferedReader.close();
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                shouldRun = false;
            }
        }
        System.out.println("Receive Thread Stopped");
    }

    public boolean hasMessage(){
        return !this.receivedData.isEmpty();
    }

    public String getMessage(){
        return this.receivedData.poll();
    }


    public void kill() {
        this.shouldRun = false;
        this.interrupt();
    }
}
