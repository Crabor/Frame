package platform.service.cxt.Context;

import platform.config.Configuration;
import platform.config.PlatformConfig;
import platform.service.cxt.WebConnector.CtxRuntimeStatus;
import platform.service.cxt.WebConnector.RedisCtxCustom;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;


public class ContextManager<T> {
    public static Map<String, ContextBuffer> ContextBufferList = new ConcurrentHashMap<>();

    public static LinkedBlockingQueue<String> errorMsgID = new LinkedBlockingQueue<String>();
    public static Map<String, Integer> checkMsgID = new HashMap<>();
    public static LinkedBlockingQueue<Message> msgBuffer = new LinkedBlockingQueue<Message>(100);
    public static LinkedBlockingQueue<Message> msgBuffer_fixed = new LinkedBlockingQueue<Message>();
    public static LinkedBlockingQueue<String> ChangeInvoked = new LinkedBlockingQueue<String>();

    public static CtxRuntimeStatus msgStatistics = new CtxRuntimeStatus();

    public static Map<String, RedisCtxCustom> CtxStatistics = new HashMap<>(); //ctx wrongnumber, correctnumber, rate, buffersize

    public static Map<String, ContextBuffer> getContextBufferList() {
        return ContextBufferList;
    }

    public static void addMsgBuffer(long index, String msg){
        while(msgBuffer.offer(new Message(index,msg))==false){
            msgBuffer.clear();
        }
        msgStatistics.addRecv();
    }
    public static void adderrorMsgID(String msgID){
        errorMsgID.add(msgID);
    }

    public static void addcheckMsgID(String msgID){
        if(checkMsgID.containsKey(msgID))
            checkMsgID.put(msgID, checkMsgID.get(msgID)+1);
        else
            checkMsgID.put(msgID, 1);
    }
    public static void adderrorMsgIDList(List<String> msgIDList){
        for(int i = 0; i<msgIDList.size(); i++) {
            String temp = msgIDList.get(i);
            if (!errorMsgID.contains(temp))
                errorMsgID.add(temp);
        }
    }

    public static void addMsgBufferFixed(long index, String msg){
        msgBuffer_fixed.add(new Message(index,msg));
    }


    public static void addMsgBufferFixed(Message message){
        msgBuffer_fixed.add(message);
    }

    public static void addList2MsgBufferFixed(LinkedList<Message> results){
        for(int i = 0; i<results.size(); i++)
            addMsgBufferFixed(results.get(i));
    }

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

    public static void fixMsgElementsUntil(long end_index) {
        //LinkedList<Message> results = new LinkedList<>();
        Message message = null;

        message = msgBuffer.peek();

            while(message!=null && message.index < end_index) {
                String Msgindex = message.index + "";
                try {
                    message = msgBuffer.take();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(checkMsgID.containsKey(Msgindex)&&checkMsgID.get(Msgindex)==Configuration.getSensorLength()) {
                    if (!errorMsgID.contains(Msgindex)) {
                        addMsgBufferFixed(message);
                        //System.out.println("Fixed: " +message.index + "---"+message);
                    } else {
                        errorMsgID.remove(Msgindex);
                        //System.out.println("Removing: " +message.index + "---"+message);
                        msgStatistics.addfilter();
                    }
                    checkMsgID.remove(Msgindex);
                }
                else {
                    System.out.println("Skipping: " +Msgindex + "---"+message.getMsg());
                    msgStatistics.addfilter();
                }
                //results.add(change);
                message = msgBuffer.peek();
            }
            //System.out.println("Error after: "+errorMsgID.toString());
        //return results;
    }

    public static LinkedList<Message> getMsgElements() {
        LinkedList<Message> results = new LinkedList<>();
        Message change = null;
        try {
            change = msgBuffer.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        results.add(change);
        for(int i = 0; i < msgBuffer.size();i++) {
            change = msgBuffer.poll();
            results.add(change);
        }
        return results;
    }
    public static LinkedList<Message> getMsgFixedElements() {
        LinkedList<Message> results = new LinkedList<>();
        Message change = null;
        change = msgBuffer_fixed.poll();
        if(change == null)
            return null;
        results.add(change);
        for(int i = 0; i < msgBuffer_fixed.size();i++) {
            change = msgBuffer_fixed.poll();
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
            String name = temp.get(i);
            ContextBufferList.put(name, new ContextBuffer(PlatformConfig.getInstace().getBuffer_raw_max(),
                    PlatformConfig.getInstace().getBuffer_clean_max()));
            CtxStatistics.put(name, new RedisCtxCustom(name, "sensor info"));
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

    public static void registRuleStatistics() {
    }
}
