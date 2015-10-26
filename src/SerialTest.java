import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataListener;
import com.pi4j.io.serial.SerialFactory;

/**
 * Created by pi on 2/25/15.
 */
public class SerialTest {

    public static void main(String[] args){
        Serial serial;
        serial = SerialFactory.createInstance();
        serial.open("/dev/ttyS1", 115200);

        serial.addListener(new SerialDataListener() {
            @Override
            public void dataReceived(SerialDataEvent serialDataEvent) {
                System.out.print(serialDataEvent.getData());
            }
        });

        System.out.println("Starting serial");
        long startTime = System.currentTimeMillis();
        while(System.currentTimeMillis() - startTime < 10000){
            serial.writeln("Testing sending data from pi to teensy!!");

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
    }
}
