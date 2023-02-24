package common.struct;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SensorData {
    Map<String, Object> data;

    public SensorData() {
        data = new HashMap<>();
    }

    public int size() {
        return data.size();
    }

    public Map<String, Object> getAll() {
        return data;
    }

    public Object get(String name) {
        return data.get(name);
    }

    public Object get() {
        return data.get("default");
    }

    public void set(Object value) {
        data.put("default", value);
    }

    public void set(String filed, Object value) {
        data.put(filed, value);
    }

    public void set(String json) {
        JSONObject jo = JSONObject.parseObject(json);
        //遍历jo的所有entry
        for (Map.Entry<String, Object> entry : jo.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            data.put(key, value);
        }
    }

    public static SensorData fromJSONString(String json) {
        JSONObject jo = JSONObject.parseObject(json);
        SensorData sensorData = new SensorData();
        //遍历jo的所有entry
        for (Map.Entry<String, Object> entry : jo.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            sensorData.data.put(key, value);
        }
        return sensorData;
    }

    private static SensorData errorData = fromJSONString("{\"default\":\"@#$%\"}");
    public static SensorData defaultErrorData() {
    	return errorData;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(data);
    }
}
