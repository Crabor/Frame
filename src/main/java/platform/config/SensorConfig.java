package platform.config;

import com.alibaba.fastjson.JSONObject;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SensorConfig {
    private String SensorType;
    private final String SensorName;
    private final List<String> fieldNames;
    private boolean isAlive = false;
    private static int aliveFreq; //定时ping
    private static int valueFreq; //定时获取value
    private String IPAddress;
    private int port;
    private final Set<String> apps = ConcurrentHashMap.newKeySet();
    private boolean registered = false;

    public SensorConfig(JSONObject object){
        try {
            SensorType = object.getString("SensorType");
        } catch (NullPointerException e) {
            SensorType = "String";
        }
        SensorName = object.getString("SensorName");
        fieldNames = Arrays.asList(object.getString("fieldNames").split(","));
//        isAlive = object.getBoolean("isAlive");
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

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    public static int getAliveFreq() {
        return aliveFreq;
    }

    public static void setAliveFreq(int freq) {
        aliveFreq = freq;
    }

    public static int getValueFreq() {
        return valueFreq;
    }

    public static void setValueFreq(int freq) {
        valueFreq = freq;
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
                ", apps=" + apps +
                ", registered=" + registered +
                '}';
    }
}
