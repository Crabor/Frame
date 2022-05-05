package platform.service.cxt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import platform.pubsub.AbstractSubscriber;
import platform.service.cxt.Config.PlatformConfig;
import platform.service.cxt.Context.Context;
import platform.service.cxt.Context.ContextManager;
import platform.service.cxt.Context.Message;
import platform.struct.GrpPrioPair;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class CxtSubscriber extends AbstractSubscriber {
    private int rawcount = 0;
    private int fixcount = 0;
    private boolean start = false;
    private long startTime = 0;

    @Override
    public void onMessage(String channel, String msg) {
        if (!start) {
            start = true;
            startTime = System.currentTimeMillis();
        }
        // 接收原始sensor数据进行处理
        // wang hui yan
        JSONObject jo = JSON.parseObject(msg);


        int index = PlatformConfig.context_index.getAndIncrement();
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        ContextManager.addMsgBuffer(index,msg);

        ContextManager.addCleanSensingContext("front", new Context(index,"front", jo.get("front"), format.format(date)));
        ContextManager.addCleanSensingContext("back", new Context(index,"back", jo.get("back"), format.format(date)));
        ContextManager.addCleanSensingContext("left", new Context(index,"left", jo.get("left"), format.format(date)));
        ContextManager.addCleanSensingContext("right", new Context(index,"right", jo.get("right"), format.format(date)));

        LinkedList<Message> send = ContextManager.getMsgFixedElements();
//
        rawcount++;
        if (send!=null) {
            fixcount++;
        }
        if (System.currentTimeMillis() - startTime > 1000) {
            System.out.println("cxt count: " + rawcount + "; fix count: " + fixcount);
            startTime = System.currentTimeMillis();
            rawcount = 0;
            fixcount = 0;
        }
//        if(send != null) {
//            for (int i = 0; i < send.size(); i++)
//                System.out.println("==================================" + send.get(i).getMsg());
//        }

        //ContextManager.addRawSensingContext();

        // 将处理后的数据返回给sensor频道
        if(send!=null) {
            for (int i = 0; i < send.size(); i++) {
                String msgNew = send.get(i).getMsg();
                publish("sensor", 1, 0, msgNew);
                //System.out.println("+++++++++++++++++++" + msgNew);
            }
       } //else {
//            publish("sensor", 1, 0, msg);
//        }
    }

    @Override
    public void onSubscribed(String channel, long subChannelCount) {

    }

    @Override
    public void onUnsubscribed(String channel, long subChannelCount) {

    }
}
