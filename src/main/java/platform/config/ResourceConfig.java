package platform.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ResourceConfig {
    private DeviceDriverConfig deviceDriverConfig;
    private DatabaseDriverConfig databaseDriverConfig;

    private Map<String, SensorConfig> sensorsConfig  = new HashMap<>();

    private Map<String, ActuatorConfig> actuatorsConfig = new HashMap<>();

    public ResourceConfig(JSONObject object) {
        this.deviceDriverConfig = new DeviceDriverConfig(object.getJSONObject("deviceDriver"));
        this.databaseDriverConfig = new DatabaseDriverConfig(object.getJSONObject("databaseDriver"));
        JSONArray sensorObj = (JSONArray) object.get("SensorConfiguration");
        for (Object sensor : sensorObj) {
            JSONObject temp = (JSONObject) sensor;
            sensorsConfig.put(temp.getString("sensorName"), new SensorConfig(temp));
        }
        JSONArray actuatorObj = (JSONArray) object.get("ActorConfiguration");
        for (Object actuator : actuatorObj) {
            JSONObject temp = (JSONObject) actuator;
            actuatorsConfig.put(temp.getString("actuatorName"), new ActuatorConfig(temp));
        }
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
