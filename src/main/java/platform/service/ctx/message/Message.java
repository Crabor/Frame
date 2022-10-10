package platform.service.ctx.message;

import platform.service.ctx.ctxChecker.context.Context;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Message {
    private final long index;
    private final Map<String, Context> contextMap;

    private final Map<String, Set<String>> appSensorInfos;

    public Message(long index) {
        this.index = index;
        this.contextMap = new HashMap<>();
        this.appSensorInfos = new HashMap<>();
    }


    public void addContext(Context context){
        contextMap.put(context.getContextId(), context);
    }

    public void addContext(String contextId, Context context){
        contextMap.put(contextId, context);
    }

    public void addAppSensorInfo(String appName, String sensorName){
        appSensorInfos.computeIfAbsent(appName, k -> new HashSet<>());
        appSensorInfos.get(appName).add(sensorName);
    }

    public Set<String> getSensorInfos(String appName){
        return appSensorInfos.get(appName);
    }

    public Map<String, Set<String>> getAppSensorInfos() {
        return appSensorInfos;
    }

    public Map<String, Context> getContextMap() {
        return contextMap;
    }

    public long getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return "Message{" +
                "index=" + index +
                ", contextMap=" + contextMap +
                ", appSensorInfos=" + appSensorInfos +
                '}';
    }
}
