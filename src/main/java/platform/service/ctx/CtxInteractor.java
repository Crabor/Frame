package platform.service.ctx;

import com.alibaba.fastjson.JSONObject;
import platform.config.CtxServerConfig;

import java.util.*;

public class CtxInteractor {

    static class SensorInfo{
        public final String State;
        public final String valueType;

        public SensorInfo(String state, String valueType) {
            State = state;
            this.valueType = valueType;
        }

        public String getState() {
            return State;
        }

        public String getValueType() {
            return valueType;
        }

        @Override
        public String toString() {
            return "SensorInfo{" +
                    "State='" + State + '\'' +
                    ", valueType='" + valueType + '\'' +
                    '}';
        }
    }
    public static volatile Map<String, SensorInfo> sensorInfoMap = null;

    private final Set<String> registeredSensors;
    private final JSONObject msgJSONObject;

    public CtxInteractor() {
        if(sensorInfoMap == null){
            synchronized (CtxInteractor.class){
                if(sensorInfoMap == null){
                    sensorInfoMap = new HashMap<>();
                    LinkedList<String> temp = CtxServerConfig.getInstace().getSensorNameList();
                    for (String name : temp) {
                        sensorInfoMap.put(name, new SensorInfo("On", "Double"));
                    }
                }
            }
        }

        this.registeredSensors = new HashSet<>();
        msgJSONObject = new JSONObject();
    }

    //查看平台所有sensors信息
    public  Map<String, SensorInfo> sensorInfos(){
        return sensorInfoMap;
    }

    //注册监听某一sensor
    public boolean registerSensor(String sensorName){
        return registeredSensors.add(sensorName);
    }

    //过滤信息
    public void filter(String channel, String msg){
        JSONObject allMsgObject = JSONObject.parseObject(msg);
        for(String sensorName : registeredSensors){
            msgJSONObject.put(sensorName, allMsgObject.getDouble(sensorName));
        }
    }

    //获取某一sensor对应的值
    public double getValue(String sensorName){
        return msgJSONObject.getDouble(sensorName);
    }

    //获取监听的所有sensor及其对应的值
    public String getMsg(){
        return msgJSONObject.toJSONString();
    }



}