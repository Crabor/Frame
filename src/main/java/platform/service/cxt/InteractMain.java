package platform.service.cxt;


import platform.service.cxt.Context.ContextBuffer;
import platform.service.cxt.Context.ContextManager;

import java.util.LinkedList;
import java.util.Map;

import static java.lang.Thread.sleep;
import static platform.service.cxt.Interactor.*;

//TODO: divide the usageMain from platform and upperAPP

public class InteractMain {
    public static void main(String [] args){
        ctxServiceStart();

        //getAllSensors();
        sensorRegist("GPSSensor-Front");
        sensorRegist("GPSSensor-Left");
        while (true){
            //printLatestData();

            LinkedList<String> data = getSensorDataAll();
            System.out.println("Begin scanning...");
            for(Map.Entry<String, ContextBuffer> entry: ContextManager.ContextBufferList.entrySet()){
                ContextBuffer buffer = entry.getValue();
                buffer.displayAllRawInQueue();
                System.out.println("---------------------");
                buffer.displayAllCleanInQueue();
                System.out.println("---------------------");
            }
            System.out.println("End scanning...");
            for(int i=0; i<data.size(); i++){
                System.out.println(data.get(i));
            }
            try {
                sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
