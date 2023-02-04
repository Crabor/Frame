package platform.config;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.ObjectUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.LockSupport;

public class SensorConfig {
    private String sensorType;
    private String sensorName;
    private List<String> fieldNames;
    private boolean isAlive = false;
    private int aliveFreq; //定时ping
    private int valueFreq; //定时获取value
    private Thread valueThread;
    private String IPAddress;
    private int port;
    private final Set<AppConfig> apps = ConcurrentHashMap.newKeySet();

    public SensorConfig(JSONObject object){
        sensorName = object.getString("sensorName");
        try {
            sensorType = object.getString("sensorType");
        } catch (NullPointerException e) {
            sensorType = "String";
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
        this.sensorName = sensorName;
        this.sensorType = sensorType;
        this.fieldNames = Arrays.asList(fieldNames.split(","));
    }

    public String getSensorType() {
        return sensorType;
    }

    public String getSensorName() {
        return sensorName;
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

    public void addApp(AppConfig app) {
        this.apps.add(app);
    }

    public void removeApp(AppConfig app) {
        this.apps.remove(app);
    }

    public Set<AppConfig> getApps() {
        return apps;
    }

    public Set<String> getAppsName() {
        Set<String> ret = new HashSet<>();
        apps.forEach(config -> {
            ret.add(config.getAppName());
        });
        return ret;
    }

    @Override
    public String toString() {
        return "SensorConfig{" +
                "sensorType='" + sensorType + '\'' +
                ", sensorName='" + sensorName + '\'' +
                ", fieldNames=" + fieldNames +
                ", aliveFreq=" + aliveFreq +
                ", valueFreq=" + valueFreq +
                '}';
    }
}
