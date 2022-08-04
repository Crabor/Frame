package platform.service.cxt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import platform.pubsub.AbstractSubscriber;
import platform.config.Configuration;
import platform.config.PlatformConfig;
import platform.config.SensorConfig;
import platform.service.cxt.Context.Context;
import platform.service.cxt.Context.ContextManager;
import platform.service.cxt.Context.Message;
import platform.service.cxt.WebConnector.RedisCtxCustom;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;

import static platform.service.cxt.Context.ContextManager.CtxStatistics;
import static platform.service.cxt.Context.ContextManager.msgStatistics;

public class CxtSubscriber extends AbstractSubscriber implements Runnable {
    private static CxtSubscriber instance;
    private Thread t;
//    public static int onMessageCount = 0;
//    public static int fixCount = 0;
    private CxtSubscriber() {
    }

    public static CxtSubscriber getInstance() {
        if (instance == null) {
            synchronized (CxtSubscriber.class) {
                if (instance == null) {
                    instance = new CxtSubscriber();
                }
            }
        }
        return instance;
    }

    @Override
    public void onMessage(String channel, String msg) {
        // 接收原始sensor数据进行处理
        // wang hui yan
        JSONObject jo = JSON.parseObject(msg);

        int index = PlatformConfig.context_index.getAndIncrement();
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        ContextManager.addMsgBuffer(index,msg);

        for (SensorConfig sensorConfig : Configuration.getListOfSensorObj()) {
            String sensorName = sensorConfig.getSensorName();
            ContextManager.addCleanSensingContext(sensorName, new Context(index,sensorName, jo.get(sensorName), format.format(date)));
        }
    }

    @Override
    public void onSubscribed(String channel, long subChannelCount) {

    }

    @Override
    public void onUnsubscribed(String channel, long subChannelCount) {

    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(50);
                LinkedList<Message> send = ContextManager.getMsgFixedElements();
                /*if(send != null) {
                    for (int i = 0; i < send.size(); i++)
                        System.out.println("==================================" + send.get(i).getMsg());
                }*/

                //publish("sensor", pair.groupId, pair.priorityId - 1, msg);

                //ContextManager.addRawSensingContext();
                // 将处理后的数据返回给sensor频道
                if(send!=null) {
//                    fixCount += send.size();
                    for (int i = 0; i < send.size(); i++) {
                        String msgNew = send.get(i).getMsg();
                        msgStatistics.addSend();
                        publish("sensor", 1, 0, msgNew);
                    }
                    //redis： "SumStatistics", SerializeUtil.serialize(msgStatistics)<---(class CtxRuntimeStatus)
                    //jedis.set(name1.getBytes(),send1);
                    //System.out.println(msgStatistics.toString());
                    publish("ctxStat", msgStatistics.toString());

                    for (Map.Entry<String, RedisCtxCustom> entry: CtxStatistics.entrySet()) {
                        //redis： "SumStatistics", SerializeUtil.serialize(msgStatistics)<---(class CtxRuntimeStatus)
                        //jedis.set(name2.getBytes(),send2);
                        publish("ctxStat", entry.getValue().toString());
                        //.out.println(entry.getKey() + ": " + entry.getValue());//redis： entry.getKey(), SerializeUtil.serialize(entry)<---(class RedisCtxCustom)
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void start() {
        if (t == null) {
            t = new Thread(this, getClass().getName());
            t.start();
        }
    }
}
