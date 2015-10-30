package org;

import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataListener;
import com.pi4j.io.serial.SerialFactory;

/**
 * Created by pi on 9/17/15.
 */
public class SerialTest {

    private Serial serial = SerialFactory.createInstance();

    public SerialTest(){
        serial.addListener(new SerialDataListener() {
            @Override
            public void dataReceived(SerialDataEvent serialDataEvent) {
                System.out.println(serialDataEvent.getData());
            }
        });

        System.out.println("Starting");
        try {
            serial.open("/dev/ttyS1", 115200);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        System.out.println("Serial port opened.");
        long startTime = System.currentTimeMillis();
        while((System.currentTimeMillis() - startTime) < 10000){
            serial.writeln("Get:2");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        serial.close();
        System.exit(0);
    }

    public static void main(String[] args){
        SerialTest serialTest = new SerialTest();
    }
}
