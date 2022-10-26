package platform.config;

import com.alibaba.fastjson.JSONObject;

import java.util.*;

public class SensorConfig {
    private String SensorType;
    private String SensorName;
    private List<String> fieldNames;
    private boolean isValid;
    private int SensorFreq; // per second;
    private String IPAddress;
    private int port;
    private Set<String> apps = new HashSet<>();
    private boolean registered = false;

    public SensorConfig(JSONObject object){
        try {
            SensorType = object.getString("SensorType");
        } catch (NullPointerException e) {
            SensorType = "String";
        }
        SensorName = object.getString("SensorName");
        fieldNames = Arrays.asList(object.getString("fieldNames").split(","));
//        isValid = object.getBoolean("isValid");
//        SensorFreq = object.getIntValue("SensorFreq");
//        IPAddress = object.getString("IPAddress");
//        port = object.getIntValue("Port");
    }

    public String getSensorType() {
        return SensorType;
    }

    public String getSensorName() {
        return SensorName;
    }

    public List<String> getFieldNames() {
        return fieldNames;
    }

    public boolean isValid() {
        return isValid;
    }

    public int getSensorFreq() {
        return SensorFreq;
    }

    public String getIPAddress() {
        return IPAddress;
    }

    public int getPort() {
        return port;
    }

    public void addApp(String app) {
        this.apps.add(app);
        registered = true;
    }

    public void removeApp(String app) {
        this.apps.remove(app);
        if (this.apps.isEmpty()) {
            registered = false;
        }
    }

    public Set<String> getApps() {
        return apps;
    }

    public boolean isRegistered() {
        return registered;
    }

    @Override
    public String toString() {
        return "SensorConfig{" +
                "SensorType='" + SensorType + '\'' +
                ", SensorName='" + SensorName + '\'' +
                ", fieldNames=" + fieldNames +
                ", isValid=" + isValid +
                ", SensorFreq=" + SensorFreq +
                ", IPAddress='" + IPAddress + '\'' +
                ", port=" + port +
                '}';
    }
}
