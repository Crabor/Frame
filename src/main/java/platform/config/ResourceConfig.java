package platform.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ResourceConfig {
    private DeviceDriverConfig deviceDriverConfig;
    private DatabaseDriverConfig databaseDriverConfig;

    private List<SensorConfig> listOfSensorObj  = new ArrayList<>();

    private List<ActorConfig> listOfActorObj = new ArrayList<>();

    public ResourceConfig(JSONObject object) {
        this.deviceDriverConfig = new DeviceDriverConfig(object.getJSONObject("deviceDriver"));
        this.databaseDriverConfig = new DatabaseDriverConfig(object.getJSONObject("databaseDriver"));
        JSONArray sensorObj = (JSONArray) object.get("SensorConfiguration");
        for(int i = 0; i < sensorObj.size(); i++) {
            JSONObject temp = (JSONObject) sensorObj.get(i);
            listOfSensorObj.add(new SensorConfig(temp));
        }
        JSONArray actorObj = (JSONArray) object.get("ActorConfiguration");
        for (int i = 0; i < actorObj.size(); i++) {
            JSONObject temp = (JSONObject) actorObj.get(i);
            listOfActorObj.add(new ActorConfig(temp));
        }
    }

    public DeviceDriverConfig getDeviceDriverConfig() {
        return deviceDriverConfig;
    }

    public DatabaseDriverConfig getDatabaseDriverConfig() {
        return databaseDriverConfig;
    }

    public List<SensorConfig> getListOfSensorObj() {
        return listOfSensorObj;
    }

    public List<ActorConfig> getListOfActorObj() {
        return listOfActorObj;
    }

    @Override
    public String toString() {
        return "ResourceConfig{" + "\n" +
                "deviceDriverConfig=" + deviceDriverConfig + "\n" +
                "databaseDriverConfig=" + databaseDriverConfig + "\n" +
                "listOfSensorObj=" + listOfSensorObj + "\n" +
                "listOfActorObj=" + listOfActorObj +
                '}';
    }
}
