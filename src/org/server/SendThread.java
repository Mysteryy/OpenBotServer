package org.server;


import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by zach on 9/9/2015.
 */
public class SendThread extends Thread {
    private Socket socket;
    private BufferedWriter bufferedWriter;
    private ConcurrentLinkedQueue<String> dataToSend = new ConcurrentLinkedQueue<String>();
    private boolean shouldRun = true;
    private String stringToSend = null;

    public SendThread(Socket socket) {
        this.socket = socket;
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        while(shouldRun){
            stringToSend = dataToSend.poll();
            if(stringToSend != null){
                System.out.println("Replying with: '" + stringToSend +"'");
                writeMessage(stringToSend);
            }
        }
        System.out.println("Send Thread Stopped");
    }

    public void sendMessage(String message){
        this.dataToSend.add(message);
    }

    private void writeMessage(String message){
        char[] charArray = message.toCharArray();
        try {
            bufferedWriter.write(charArray);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void kill() {
        this.shouldRun = false;
    }
}
