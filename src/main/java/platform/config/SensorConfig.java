package platform.config;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.ObjectUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.LockSupport;

public class SensorConfig {
    private String SensorType;
    private String SensorName;
    private List<String> fieldNames;
    private boolean isAlive = false;
    private int aliveFreq; //定时ping
    private int valueFreq; //定时获取value
    private Thread valueThread;
    private String IPAddress;
    private int port;
    private final Set<String> apps = ConcurrentHashMap.newKeySet();
    private boolean registered = false;

    public SensorConfig(JSONObject object){
        SensorName = object.getString("SensorName");
        try {
            SensorType = object.getString("SensorType");
        } catch (NullPointerException e) {
            SensorType = "String";
        }
        try {
            fieldNames = Arrays.asList(object.getString("fieldNames").split(","));
        } catch (NullPointerException e) {

        }
        try {
            aliveFreq = object.getInteger("aliveFreq");
        } catch (NullPointerException e) {
            aliveFreq = 1;
        }
        try {
            valueFreq = object.getInteger("valueFreq");
        } catch (NullPointerException e) {
            valueFreq = 0;
        }
    }

    public SensorConfig(String sensorName, String sensorType, String fieldNames) {
        SensorName = sensorName;
        SensorType = sensorType;
        this.fieldNames = Arrays.asList(fieldNames.split(","));
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

    public int getAliveFreq() {
        return aliveFreq;
    }

    public void setAliveFreq(int freq) {
        aliveFreq = freq;
    }

    public int getValueFreq() {
        return valueFreq;
    }

    public void setValueFreq(int freq) {
        valueFreq = freq;
        if (freq > 0 && valueThread != null) {
            LockSupport.unpark(valueThread);
        }
    }

    public String getIPAddress() {
        return IPAddress;
    }

    public int getPort() {
        return port;
    }

    public void setValueThread(Thread valueThread) {
        this.valueThread = valueThread;
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
                ", aliveFreq=" + aliveFreq +
                ", valueFreq=" + valueFreq +
                '}';
    }
}
