package platform.service.ctx.message;

import com.alibaba.fastjson.JSONObject;
import platform.config.AppConfig;
import platform.config.Configuration;
import platform.config.CtxServerConfig;
import platform.service.ctx.ctxChecker.context.Context;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MessageHandler {

    public static Message jsonObject2Message(JSONObject msgObj){
        long index = Long.parseLong(msgObj.getString("index"));
        Message message = new Message(index);
        for(String sensorName : msgObj.keySet()){
            if(sensorName.equals("index")){
                continue;
            }
            if(msgObj.getString(sensorName).equals("")){
                //被丢弃或者没有获得
                message.addContext(sensorName + "_" + index, null);
            }
            else{
                Context context = buildContext(index, sensorName, msgObj.getString(sensorName));
                message.addContext(context);
            }
            Set<AppConfig> appSet = Configuration.getAppsBySensorName(sensorName);
            for(AppConfig app : appSet){
                message.addAppSensorInfo(app.getAppName(), sensorName);
            }
        }
        // 如果message中包含的所有context 都是null，那么返回null
        for(Context context : message.getContextMap().values()){
            if(context != null){
                return message;
            }
        }
        return null;
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

    public static Context fixAndCloneContext(final Context context, final Map<String, String> fixingPairs){
        assert context != null;
        assert fixingPairs != null;
        Context retContext = new Context();
        retContext.setContextId(context.getContextId());
        for(String field : context.getContextFields().keySet()){
            if(fixingPairs.containsKey(field)){
                retContext.getContextFields().put(field, fixingPairs.get(field));
            }
            else{
                retContext.getContextFields().put(field, context.getContextFields().get(field));
            }
        }
        return retContext;
    }

    public static JSONObject buildPubJSONObjWithIndex(final Message sendingMsg, final Set<String> sensorInfos){
        JSONObject pubMsgJsonObj = new JSONObject();
        long index = sendingMsg.getIndex();
        pubMsgJsonObj.put("index", String.valueOf(index));
        for(String sensorName : sensorInfos){
            Context context = sendingMsg.getContextMap().get(sensorName + "_" + index);
            pubMsgJsonObj.put(sensorName, context == null ? "" : context.toMsgString());
        }
        return pubMsgJsonObj;
    }

    public static JSONObject buildPubJSONObjWithoutIndex(final Message sendingMsg, final Set<String> sensorInfos){
        JSONObject pubMsgJsonObj = new JSONObject();
        long index = sendingMsg.getIndex();
        for(String sensorName : sensorInfos){
            Context context = sendingMsg.getContextMap().get(sensorName + "_" + index);
            pubMsgJsonObj.put(sensorName, context == null ? "" : context.toMsgString());
        }
        return pubMsgJsonObj;
    }

    //框架2.1
    public static Message buildMsg(long msgIndex, String sensorName, JSONObject msgObj){
        Message message = new Message(msgIndex);
        message.addContext(buildContext(msgIndex, sensorName, msgObj));
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

    public static Map.Entry<String, JSONObject> buildPubMsgObj(final Message sendingMsg){
        assert sendingMsg.getContextMap().size() == 1;
        String ctxId = List.copyOf(sendingMsg.getContextMap().keySet()).get(0);
        Context context = sendingMsg.getContextMap().get(ctxId);
        if(context == null){
            return null;
        }
        else{
            JSONObject msgObj = new JSONObject();
            for(String field : context.getContextFields().keySet()){
                msgObj.put(field, context.getContextFields().get(field));
            }
            return new AbstractMap.SimpleEntry<>(ctxId.split("_")[0], msgObj);
        }
    }
}
