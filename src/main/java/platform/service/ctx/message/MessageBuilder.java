package platform.service.ctx.message;

import com.alibaba.fastjson.JSONObject;
import platform.config.CtxServerConfig;
import platform.service.ctx.ctxChecker.context.Context;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class MessageBuilder {

    public static final AtomicLong msgIndex=  new AtomicLong();

    public static Message jsonObject2Message(JSONObject msgObj){
        long index = -1;
        synchronized (msgIndex){
            index = msgIndex.getAndIncrement();
        }
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
        context.setContextId(sensorName + "_" + index);
        String[] values = data.split(",");
        List<String> sensorFields = CtxServerConfig.getInstance().getSensorConfigMap().get(sensorName).getFieldNames();
        for(int i = 0 ; i< sensorFields.size(); ++i){
            context.getContextFields().put(sensorFields.get(i), values[i]);
        }
        return context;
    }

}
