package platform.config;

import com.alibaba.fastjson.JSONObject;

public class ResourceConfig {
    private DeviceDriverConfig deviceDriverConfig;
    private DatabaseDriverConfig databaseDriverConfig;

    public ResourceConfig(JSONObject object) {
        this.deviceDriverConfig = new DeviceDriverConfig(object.getJSONObject("deviceDriver"));
        this.databaseDriverConfig = new DatabaseDriverConfig(object.getJSONObject("databaseDriver"));
    }

    public DeviceDriverConfig getDeviceDriverConfig() {
        return deviceDriverConfig;
    }

    public DatabaseDriverConfig getDatabaseDriverConfig() {
        return databaseDriverConfig;
    }

    @Override
    public String toString() {
        return "ResourceConfig{" +
                "deviceDriverConfig=" + deviceDriverConfig +
                ", databaseDriverConfig=" + databaseDriverConfig +
                '}';
    }
}
