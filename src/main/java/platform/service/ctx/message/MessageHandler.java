package platform.service.ctx.message;

import com.alibaba.fastjson.JSONObject;
import platform.service.ctx.ctxChecker.context.Context;

import java.util.AbstractMap;
import java.util.Map;

public class MessageHandler {

    //框架2.1
    public static Message buildMessage(long msgIndex, String sensorName, JSONObject dataObj){
        Message message = new Message(msgIndex);
        message.addContext(buildContext(msgIndex, sensorName, dataObj));
        return message;
    }

    private static Context buildContext(long index, String sensorName, JSONObject dataObj){
        Context context = new Context();
        context.setContextId(sensorName + "_" + index);
        for(String filed : dataObj.keySet()){
            context.getContextFields().put(filed, dataObj.getString(filed));
        }
        return context;
    }

    public static Context cloneContext(final Context context){
        assert context != null;
        Context retContext = new Context();
        retContext.setContextId(context.getContextId());
        for(Map.Entry<String, String> field : context.getContextFields().entrySet()){
            retContext.getContextFields().put(field.getKey(), field.getValue());
        }
        return retContext;
    }

    public static Map.Entry<String, JSONObject> buildValidatedMessageJsonObj(final Message validatedMsg){
        Context context = validatedMsg.getContext();
        if(context == null){
            return null;
        }
        else{
            JSONObject dataObj = new JSONObject();
            for(String field : context.getContextFields().keySet()){
                dataObj.put(field, context.getContextFields().get(field));
            }
            return new AbstractMap.SimpleEntry<>(context.getContextId().split("_")[0], dataObj);
        }
    }
}
