package platform.service.cxt.Context;

import platform.service.cxt.Context.Context;
import platform.service.cxt.Config.PlatformConfig;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;


public class ContextManager<T> {
    public static Map<String, ContextBuffer> ContextBufferList = new ConcurrentHashMap<>();

    public static LinkedBlockingQueue<String> ChangeInvoked = new LinkedBlockingQueue<String>();

    public static LinkedBlockingQueue<String> getChangeInvoked() {

        //System.out.println("Get change: " + ChangeInvoked);
        return ChangeInvoked;
    }
    public static LinkedList<String> getChangeInvokedElements() {
        LinkedList<String> results = new LinkedList<>();
        String change = null;
        try {
            change = ChangeInvoked.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        results.add(change);
        for(int i = 0; i < ChangeInvoked.size();i++) {
            change = ChangeInvoked.poll();
            results.add(change);
        }
        return results;
    }

    public static void registContextManager(String name){
        ContextBufferList.put(name, new ContextBuffer(PlatformConfig.getInstace().getBuffer_raw_max(),
                PlatformConfig.getInstace().getBuffer_clean_max()));
    }
    public static void registContextManagerAll() {
        LinkedList<String> temp = PlatformConfig.getInstace().getSensorNameList();
        for (int i = 0; i < temp.size(); i++) {
            ContextBufferList.put(temp.get(i), new ContextBuffer(PlatformConfig.getInstace().getBuffer_raw_max(),
                    PlatformConfig.getInstace().getBuffer_clean_max()));
        }
    }
    public static void addRawSensingContext(String sensorName, Context context){
        ContextBufferList.get(sensorName).insertRawData(context);
    }
    public static Context pollRawSensingContext(String sensorName){
        return ContextBufferList.get(sensorName).pollFirstRawData();
    }
    public static LinkedList<Context> pollRawSensingContextAll(){
        LinkedList<Context> temp = new LinkedList<>();
        for(Map.Entry<String, ContextBuffer> entry: ContextBufferList.entrySet()) {
            Context c = pollRawSensingContext(entry.getKey());
            if(c!=null)
                temp.add(c);
        }
        return temp;
    }
    public static void addCleanSensingContext(String sensorName, Context context){
        //System.out.println(context.toString());
        ContextBufferList.get(sensorName).insertCleanData(context);
    }
    public static Context pollCleanSensingContext(String sensorName){
        return ContextBufferList.get(sensorName).pollFirstCleanData();
    }
    public static LinkedList<Context> pollCleanSensingContextAll(){
        LinkedList<Context> temp = new LinkedList<>();
        for(Map.Entry<String, ContextBuffer> entry: ContextBufferList.entrySet()) {
            Context c = pollCleanSensingContext(entry.getKey());
            if (c != null)
                temp.add(c);
        }
        return temp;
    }
}
