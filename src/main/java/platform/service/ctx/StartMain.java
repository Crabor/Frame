package platform.service.ctx;


import platform.service.ctx.Context.ContextBuffer;
import platform.service.ctx.Context.ContextManager;

import java.util.Map;

import static platform.service.ctx.Interactor.sensorRegist;
import static java.lang.Thread.sleep;
import static platform.service.ctx.Interactor.ctxServiceStart;
import static platform.service.ctx.Interactor.printAllSensors;

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
