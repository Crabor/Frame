package platform.service.cxt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import platform.pubsub.AbstractSubscriber;
import platform.service.cxt.Config.PlatformConfig;
import platform.service.cxt.Context.Context;
import platform.service.cxt.Context.ContextManager;
import platform.service.cxt.Context.Message;
import platform.service.cxt.WebConnector.RedisCtxCustom;
import platform.service.cxt.WebConnector.SerializeUtil;
import platform.struct.GrpPrioPair;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;

import static platform.service.cxt.Context.ContextManager.CtxStatistics;
import static platform.service.cxt.Context.ContextManager.msgStatistics;

public class CxtSubscriber extends AbstractSubscriber {

    @Override
    public void onMessage(String channel, String msg) {
        // 接收原始sensor数据进行处理
        // wang hui yan
        JSONObject jo = JSON.parseObject(msg);


        int index = PlatformConfig.context_index.getAndIncrement();
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        System.out.println(jo);

        ContextManager.addMsgBuffer(index,msg);

        ContextManager.addCleanSensingContext("front", new Context(index,"front", jo.get("front"), format.format(date)));
        ContextManager.addCleanSensingContext("back", new Context(index,"back", jo.get("back"), format.format(date)));
        ContextManager.addCleanSensingContext("left", new Context(index,"left", jo.get("left"), format.format(date)));
        ContextManager.addCleanSensingContext("right", new Context(index,"right", jo.get("right"), format.format(date)));

        LinkedList<Message> send = ContextManager.getMsgFixedElements();
        /*if(send != null) {
            for (int i = 0; i < send.size(); i++)
                System.out.println("==================================" + send.get(i).getMsg());
        }*/

        //publish("sensor", pair.groupId, pair.priorityId - 1, msg);

        //ContextManager.addRawSensingContext();

        GrpPrioPair pair = getGrpPrioPair(channel); //获取该订阅者在sensor频道的分组及优先级信息
        // 将处理后的数据返回给sensor频道
        if(send!=null) {
            for (int i = 0; i < send.size(); i++) {
                String msgNew = send.get(i).getMsg();
                msgStatistics.addSend();
                if (pair != null) {
                    // pair.priorityId - 1是为了将数据发送给比自己优先级低的订阅者，防止被自己拦截
                    publish("sensor", pair.groupId, pair.priorityId - 1, msgNew);
                    System.out.println("+++++++++++++++++++" + msgNew );

                }
            }
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

    @Override
    public void onSubscribed(String channel, long subChannelCount) {

    }

    @Override
    public void onUnsubscribed(String channel, long subChannelCount) {

    }
}
