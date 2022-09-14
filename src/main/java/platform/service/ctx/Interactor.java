package platform.service.ctx;

import platform.config.Configuration;
import platform.config.CtxServerConfig;
import platform.service.ctx.Context.Context;
import platform.config.SensorConfig;

import platform.service.ctx.Context.ContextBuffer;
import platform.service.ctx.Context.ContextManager;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;

public class Interactor {

    public static void ctxServiceStart(){
        Configuration.analyzer("Resources/Configuration");
        //ListenManager.ListenReady(Configuration.getListOfSensorObj());

        //Thread checkerThread = new Thread(new CheckerBuilder(CtxServerConfig.getInstace()));
        //checkerThread.setPriority(Thread.MAX_PRIORITY);
        //checkerThread.start();

    }
    public static void sensorRegist(String name){
        ContextManager.registContextManager(name);
        //System.out.println("Register buffer for "+name);
        //ListenManager.ListenStart(name);
    }
    public static String getSensorData(String name){
        if(CtxServerConfig.getInstace().isCtxCleanOn()) {
            Context c = ContextManager.pollCleanSensingContext(name);
            return (c!=null?c.toString():null);
        }
        else {
            Context c = ContextManager.pollRawSensingContext(name);
            return (c != null ? c.toString() : null);
        }
    }
    public static LinkedList<String> getSensorDataAll(){
        LinkedList<String> result = new LinkedList<>();
        LinkedList<Context> temp;
        if(CtxServerConfig.getInstace().isCtxCleanOn())
            temp = ContextManager.pollCleanSensingContextAll();
        else
            temp = ContextManager.pollRawSensingContextAll();

        if(temp.size() > 0)
            for(int i=0; i<temp.size();i++)
                result.add(temp.get(i).toString());
        return result;
    }
    public static void ruleRegistAll(){
        ContextManager.registRuleStatistics();
//        LinkedList<String> temp = CtxServerConfig.getInstace().getSensorNameList();
//        for (int i=0; i< temp.size(); i++){
//            sensorRegist(temp.get(i));
//        }

    }
    public static void sensorRegistAll(){
        ContextManager.registContextManagerAll();
//        LinkedList<String> temp = CtxServerConfig.getInstace().getSensorNameList();
//        for (int i=0; i< temp.size(); i++){
//            sensorRegist(temp.get(i));
//        }

    }
    public static void printAllSensors(){
        List<SensorConfig> sensorConfigs =  Configuration.getResourceConfig().getListOfSensorObj();
        for(int i = 0; i<sensorConfigs.size(); i++)
            System.out.println(sensorConfigs.get(i).toString());
    }
    public static void printLatestData(){
        LinkedList<Context> temp = ContextManager.pollRawSensingContextAll();
        for(int i=0; i<temp.size();i++) {
            System.out.println(temp.get(i).toString());
        }
    }
    public static void main(String[] args){
        ctxServiceStart();
        //getAllSensors();
        sensorRegist("GPSSensor-Front");
        sensorRegist("GPSSensor-Left");
        System.out.println("Hello" + ContextManager.ContextBufferList.toString());

        while (true){
            //printLatestData();
            System.out.println("Begin scanning...");
            for(Map.Entry<String, ContextBuffer> entry: ContextManager.ContextBufferList.entrySet()){
                ContextBuffer buffer = entry.getValue();
                buffer.displayAllRawInQueue();
                System.out.println("---------------------");
                buffer.displayAllCleanInQueue();
                System.out.println("---------------------");
            }
            System.out.println("End scanning...");
            //System.out.println(ContextManager.pollRawSensingContextAll());
            /*System.out.println(ContextManager.pollRawSensingContext("GPSSensor-Front"));
            System.out.println("---------------------");
            System.out.println(ContextManager.pollCleanSensingContext("GPSSensor-Front"));
            System.out.println("----------------------------------------");
            System.out.println(ContextManager.pollRawSensingContext("GPSSensor-Left"));
            System.out.println("---------------------");
            System.out.println(ContextManager.pollCleanSensingContext("GPSSensor-Left"));*/
            getSensorDataAll();
            System.out.println("Begin scanning...");
            for(Map.Entry<String, ContextBuffer> entry: ContextManager.ContextBufferList.entrySet()){
                ContextBuffer buffer = entry.getValue();
                buffer.displayAllRawInQueue();
                System.out.println("---------------------");
                buffer.displayAllCleanInQueue();
                System.out.println("---------------------");
            }
            System.out.println("End scanning...");
            try {
                sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
