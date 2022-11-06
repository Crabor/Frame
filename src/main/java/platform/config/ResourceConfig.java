package platform.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceConfig {
    private DeviceDriverConfig deviceDriverConfig;
    private DatabaseDriverConfig databaseDriverConfig;

    private Map<String, SensorConfig> sensorsConfig  = new HashMap<>();

    private Map<String, ActuatorConfig> actuatorsConfig = new HashMap<>();

    public ResourceConfig(JSONObject object) {
        this.deviceDriverConfig = new DeviceDriverConfig(object.getJSONObject("deviceDriver"));
        this.databaseDriverConfig = new DatabaseDriverConfig(object.getJSONObject("databaseDriver"));
        JSONObject sensorObj = (JSONObject) object.get("SensorConfiguration");
        JSONArray sensors = sensorObj.getJSONArray("sensors");
        JSONObject sensorFreq = sensorObj.getJSONObject("freq");
        for (Object sensor : sensors) {
            JSONObject temp = (JSONObject) sensor;
            sensorsConfig.put(temp.getString("SensorName"), new SensorConfig(temp));
        }
        SensorConfig.setAliveFreq(sensorFreq.getIntValue("aliveFreq"));
        SensorConfig.setValueFreq(sensorFreq.getIntValue("valueFreq"));
        JSONObject actuatorObj = (JSONObject) object.get("ActorConfiguration");
        JSONArray actuators = actuatorObj.getJSONArray("actuators");
        JSONObject actuatorFreq = actuatorObj.getJSONObject("freq");
        for (Object actuator : actuators) {
            JSONObject temp = (JSONObject) actuator;
            actuatorsConfig.put(temp.getString("ActuatorName"), new ActuatorConfig(temp));
        }
        ActuatorConfig.setAliveFreq(actuatorFreq.getIntValue("aliveFreq"));
    }

    public DeviceDriverConfig getDeviceDriverConfig() {
        return deviceDriverConfig;
    }

    public DatabaseDriverConfig getDatabaseDriverConfig() {
        return databaseDriverConfig;
    }

    public Map<String, SensorConfig> getSensorsConfig() {
        return sensorsConfig;
    }

    public Map<String, ActuatorConfig> getActuatorsConfig() {
        return actuatorsConfig;
    }

    @Override
    public String toString() {
        return "ResourceConfig{" + "\n" +
                "deviceDriverConfig=" + deviceDriverConfig + "\n" +
                "databaseDriverConfig=" + databaseDriverConfig + "\n" +
                "sensorsConfig=" + sensorsConfig + "\n" +
                "actuatorsConfig=" + actuatorsConfig +
                '}';
    }
}
