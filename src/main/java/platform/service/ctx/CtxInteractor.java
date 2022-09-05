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
    public static volatile Map<String, SensorInfo> supportedSensors = null;

    private final Map<String, SensorInfo> registeredSensors;
    private final JSONObject msgJSONObject;

    public CtxInteractor() {
        if(supportedSensors == null){
            synchronized (CtxInteractor.class){
                if(supportedSensors == null){
                    supportedSensors = new HashMap<>();
                    LinkedList<String> temp = CtxServerConfig.getInstace().getSensorNameList();
                    for (String name : temp) {
                        supportedSensors.put(name, new SensorInfo("On", "Double"));
                    }
                }
            }
        }

        this.registeredSensors = new HashMap<>();
        msgJSONObject = new JSONObject();
    }

    //查看平台所有sensor信息
    public Map<String, SensorInfo> getSupportedSensors(){
        return supportedSensors;
    }

    //查看注册监听的所有sensor信息
    public Map<String, SensorInfo> getRegisteredSensors(){return registeredSensors;}

    //注册监听某一sensor
    public boolean registerSensor(String sensorName){
        if(supportedSensors.containsKey(sensorName)){
            synchronized (CtxInteractor.class){
                registeredSensors.put(sensorName, supportedSensors.get(sensorName));
            }
            return true;
        }
        else{
            return false;
        }
    }

    //过滤信息
    public void filter(String channel, String msg){
        JSONObject allMsgObject = JSONObject.parseObject(msg);
        for(String sensorName : registeredSensors.keySet()){
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