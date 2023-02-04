package platform.config;

import com.alibaba.fastjson.JSONObject;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ActuatorConfig {
    private final String actuatorName;
    private String actuatorType;
    private boolean isAlive = false;
    private int aliveFreq;
    private final Set<AppConfig> apps = ConcurrentHashMap.newKeySet();

    public ActuatorConfig(JSONObject object) {
        actuatorName = object.getString("actuatorName");
        try {
            aliveFreq = object.getInteger("aliveFreq");
        } catch (NullPointerException e) {
            aliveFreq = 1;
        }
        try {
            actuatorType = object.getString("actuatorType");
        } catch (NullPointerException e) {
            actuatorType = "String";
        }
    }

    public int getAliveFreq() {
        return aliveFreq;
    }

    public void setAliveFreq(int freq) {
        aliveFreq = freq;
    }

    public String getActuatorName() {
        return actuatorName;
    }

    public String getActuatorType() {
        return actuatorType;
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

    public void setAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    public boolean isAlive() {
        return isAlive;
    }

    @Override
    public String toString() {
        return "ActuatorConfig{" +
                "actuatorName='" + actuatorName + '\'' +
                ", actuatorType='" + actuatorType + '\'' +
                ", aliveFreq=" + aliveFreq +
                '}';
    }
}
