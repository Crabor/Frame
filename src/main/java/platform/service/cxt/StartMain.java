package platform.service.cxt;


import platform.service.cxt.Context.ContextBuffer;
import platform.service.cxt.Context.ContextManager;

import java.util.Map;

import static platform.service.cxt.Interactor.sensorRegist;
import static java.lang.Thread.sleep;
import static platform.service.cxt.Interactor.ctxServiceStart;
import static platform.service.cxt.Interactor.printAllSensors;

public class StartMain {
    public static void main(String[] args) throws InterruptedException {
        ctxServiceStart();

        printAllSensors();
        sensorRegist("GPSSensor-Front");
        sensorRegist("GPSSensor-Left");
        while (true){
            System.out.println("Begin scanning...");
            for(Map.Entry<String, ContextBuffer> entry: ContextManager.ContextBufferList.entrySet()){
                ContextBuffer buffer = entry.getValue();
                buffer.displayAllRawInQueue();
                System.out.println("---------------------");
                buffer.displayAllCleanInQueue();
                System.out.println("---------------------");
            }
            System.out.println("End scanning...");
            sleep(2000);
        }
    }
}
