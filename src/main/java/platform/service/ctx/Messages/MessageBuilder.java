package platform.service.ctx.Messages;

import com.alibaba.fastjson.JSONObject;
import platform.config.CtxServerConfig;
import platform.service.ctx.Contexts.Context;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class MessageBuilder {

    public static AtomicLong msgIndex=  new AtomicLong();

    public static Message jsonObject2Message(JSONObject msgObj){
        long index = msgIndex.getAndIncrement();
        long timestamp = new Date().getTime();
        Message message = new Message(index, timestamp);
        for(String sensorName : msgObj.keySet()){
            Context context = buildContext(index, sensorName, msgObj.getString(sensorName));
            message.addContext(context);
        }
        return message;
    }

    private static Context buildContext(long index, String sensorName, String data){
        Context context = new Context();
        context.setCtx_id(sensorName + "_" + index);
        String[] values = data.split(",");
        List<String> sensorFields = CtxServerConfig.getInstance().getSensorConfigMap().get(sensorName).getFieldNames();
        for(int i = 0 ; i< sensorFields.size(); ++i){
            context.getCtx_fields().put(sensorFields.get(i), values[i]);
        }
        return context;
    }

}
