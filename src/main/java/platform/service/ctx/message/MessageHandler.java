package platform.service.ctx.message;

import com.alibaba.fastjson.JSONObject;
import platform.config.CtxServerConfig;
import platform.service.ctx.ctxChecker.context.Context;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class MessageHandler {

    //如果支持新增ctx，那应该改成double
    public static final AtomicLong msgIndex=  new AtomicLong();

    public static Message jsonObject2Message(JSONObject msgObj){
        long index = msgIndex.getAndIncrement();
        Message message = new Message(index);
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

    public static Context cloneContext(final Context context){
        Context retContext = new Context();
        retContext.setContextId(context.getContextId());
        for(Map.Entry<String, String> field : context.getContextFields().entrySet()){
            retContext.getContextFields().put(field.getKey(), field.getValue());
        }
        return retContext;
    }

    public static Context fixAndCloneContext(final Context context, String sensorName, String data){
        Context retContext = new Context();
        retContext.setContextId(context.getContextId());
        String[] values = data.split(",");
        List<String> sensorFields = CtxServerConfig.getInstance().getSensorConfigMap().get(sensorName).getFieldNames();
        for(int i = 0 ; i< sensorFields.size(); ++i){
            retContext.getContextFields().put(sensorFields.get(i), values[i]);
        }
        return retContext;
    }
}
