package platform.service.cxt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import platform.pubsub.AbstractSubscriber;
import platform.service.cxt.Config.PlatformConfig;
import platform.service.cxt.Context.ContextManager;
import platform.struct.GrpPrioPair;

public class CxtSubscriber extends AbstractSubscriber {
    @Override
    public void onMessage(String channel, String msg) {
        // 接收原始sensor数据进行处理
        // wang hui yan
        JSONObject jo = JSON.parseObject(msg);
        jo.put("cxt", "yes");
        String msgNew = jo.toString();


        int index = PlatformConfig.context_index.getAndIncrement();

        //ContextManager.addRawSensingContext();

        // 将处理后的数据返回给sensor频道
        GrpPrioPair pair = getGrpPrioPair(channel); //获取该订阅者在sensor频道的分组及优先级信息
        if (pair != null) {
            // pair.priorityId - 1是为了将数据发送给比自己优先级低的订阅者，防止被自己拦截
            publish("sensor", pair.groupId, pair.priorityId - 1, msgNew);
        }
    }

    @Override
    public void onSubscribed(String channel, long subChannelCount) {

    }

    @Override
    public void onUnsubscribed(String channel, long subChannelCount) {

    }
}
