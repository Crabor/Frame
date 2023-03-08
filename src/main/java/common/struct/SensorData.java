package common.struct;

import com.alibaba.fastjson.JSONObject;
import common.struct.enumeration.SensorDataType;

import java.util.HashMap;
import java.util.Map;

public class SensorData {
    SensorDataType type;
    Map<String, Object> data;

    public SensorData() {
        this(SensorDataType.MSG);
    }

    public SensorData(String json) {
        this(SensorDataType.MSG, json);
    }

    public SensorData(String[] fields, Object[] values) {
    	this(SensorDataType.MSG, fields, values);
    }

    public SensorData(String filed, Object value) {
    	this(SensorDataType.MSG, filed, value);
    }

    public SensorData(SensorDataType type) {
        this.type = type;
        data = new HashMap<>();
    }

    public SensorData(SensorDataType type, String json) {
        this.type = type;
        data = new HashMap<>();
        setData(json);
    }

    public SensorData(SensorDataType type, String[] fields, Object[] values) {
    	this.type = type;
    	data = new HashMap<>();
    	setData(fields, values);
    }

    public SensorData(SensorDataType type, String filed, Object value) {
    	this.type = type;
    	data = new HashMap<>();
    	setData(filed, value);
    }

    public SensorDataType getType() {
        return type;
    }

    public int size() {
        return data.size();
    }

    public Map<String, Object> getAllData() {
        return data;
    }

    public Object getData(String name) {
        return data.get(name);
    }

    public Object getData() {
        return data.get("default");
    }

    public void setData(Object value) {
        data.put("default", value);
    }

    public void setData(String filed, Object value) {
        data.put(filed, value);
    }

    public void setData(String[] fields, Object[] values) {
    	for(int i = 0; i < fields.length; i++) {
    		data.put(fields[i], values[i]);
    	}
    }

    public void setData(String json) {
        JSONObject jo = JSONObject.parseObject(json);
        //遍历jo的所有entry
        for (Map.Entry<String, Object> entry : jo.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            data.put(key, value);
        }
    }

    //fromJSONString
    public static SensorData fromJSONString(String json) {
        JSONObject jo = JSONObject.parseObject(json);
        String type = jo.getString("sensor_data_type");
        SensorData sensorData = new SensorData(type == null ? SensorDataType.MSG : SensorDataType.fromString(type));
        //遍历jo除了sensor_data_type的所有entry，使用filter
        jo.entrySet().stream().filter(entry -> !entry.getKey().equals("sensor_data_type")).forEach(entry -> {
            String key = entry.getKey();
            Object value = entry.getValue();
            sensorData.setData(key, value);
        });
        return sensorData;
    }

    private static final SensorData errorData = fromJSONString("{\"default\":\"@#$%\"}");
    public static SensorData defaultErrorData() {
    	return errorData;
    }

    @Override
    public String toString() {
        //把Map转换为JSONObject
        JSONObject jo = new JSONObject(data);
        if (type != SensorDataType.MSG) {
            jo.put("sensor_data_type", type.toString());
        }
        return jo.toJSONString();
    }
}
