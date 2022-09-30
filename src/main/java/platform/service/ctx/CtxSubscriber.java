package platform.service.ctx;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import platform.config.CtxServerConfig;
import platform.pubsub.AbstractSubscriber;
import platform.config.Configuration;
import platform.config.SensorConfig;
import platform.service.ctx.ctxChecker.CMID.builder.CheckerBuilder;
import platform.service.ctx.Context_bk.Context;
import platform.service.ctx.Context_bk.ContextManager;
import platform.service.ctx.Context_bk.Message;
import platform.service.ctx.WebConnector.RedisCtxCustom;
import platform.struct.GrpPrioPair;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;

import static platform.service.ctx.Context_bk.ContextManager.CtxStatistics;
import static platform.service.ctx.Context_bk.ContextManager.msgStatistics;
import static platform.service.ctx.Interactor.ruleRegistAll;
import static platform.service.ctx.Interactor.sensorRegistAll;

public class CtxSubscriber extends AbstractSubscriber implements Runnable {
    private static CtxSubscriber instance;
    private Thread t;

//    private static final Log logger = LogFactory.getLog(CxtSubscriber.class);
//    public static int onMessageCount = 0;
//    public static int fixCount = 0;
    private CtxSubscriber() {
        ruleRegistAll();
        sensorRegistAll();
        //CMID
        Thread checkerThread = new Thread(new CheckerBuilder(CtxServerConfig.getInstace()));
        checkerThread.setPriority(Thread.MAX_PRIORITY);
        checkerThread.start();
        //INFuse
//        Thread checkerThread = new Thread(new Starter(CtxServerConfig.getInstace()));
//        checkerThread.setPriority(Thread.MAX_PRIORITY);
//        checkerThread.start();

    }

    public static CtxSubscriber getInstance() {
        if (instance == null) {
            synchronized (CtxSubscriber.class) {
                if (instance == null) {
                    instance = new CtxSubscriber();
                }
            }
        }
        return instance;
    }

    @Override
    public void onMessage(String channel, String msg) {
        // 接收原始sensor数据进行处理
        // wang hui yan
        logger.debug("ctx recv: " + msg);
        JSONObject jo = JSON.parseObject(msg);



        int index = CtxServerConfig.ctxIndex.getAndIncrement();
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        ContextManager.addMsgBuffer(index,msg);

        for (SensorConfig sensorConfig : Configuration.getResourceConfig().getListOfSensorObj()) {
            String sensorName = sensorConfig.getSensorName();
            ContextManager.addCleanSensingContext(sensorName, new Context(index,sensorName, jo.get(sensorName), format.format(date)));
        }
    }

    @Override
    public void run() {
        while (true) {
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
                    GrpPrioPair p = getGrpPrioPair("sensor");
                    logger.debug("ctx send: " + msgNew);
                    publish("sensor", p.groupId, p.priorityId - 1, msgNew);
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
        }
    }

    public void start() {
        if (t == null) {
            t = new Thread(this, getClass().getName());
            t.start();
        }
    }
}
