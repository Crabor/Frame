package platform.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import platform.app.AbstractApp;

import java.util.*;

public class AppConfig {
    private String appName;
    private List<SubConfig> subConfigs = new ArrayList<>();
    private Set<String> sensors = new HashSet<>();

    public AppConfig(JSONObject object) {
        this.appName = object.getString("appName");
        JSONArray subs = object.getJSONArray("subscribe");
        for (int i = 0; i < subs.size(); i++) {
            JSONObject sub = subs.getJSONObject(i);
            subConfigs.add(new SubConfig(sub));
        }
        try {
            JSONArray rss = object.getJSONArray("registerSensors");
            for (int i = 0; i < rss.size(); i++) {
                String rs = rss.getString(i);
                registerSensor(rs);
            }
        } catch (Exception e) {

        }
    }

    public String getAppName() {
        return appName;
    }

    public List<SubConfig> getSubConfigs() {
        return subConfigs;
    }

    public Set<String> getSensors() {
        return sensors;
    }

    private void addSensors(Set<String> sensors) {
        this.sensors.addAll(sensors);
    }

    private void removeSensors(Set<String> sensors) {
        this.sensors.removeAll(sensors);
    }

    public void registerSensor(String... sensors) {
        try {
            addSensors(Set.of(sensors));
            for (String sensor : sensors) {
                Configuration.getResourceConfig().getSensorsConfig().get(sensor).addApp(appName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelSensor(String... sensors) {
        try {
            removeSensors(Set.of(sensors));
            for (String sensor : sensors) {
                Configuration.getResourceConfig().getSensorsConfig().get(sensor).removeApp(appName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "AppConfig{" +
                "appName='" + appName + '\'' +
                ", subConfigs=" + subConfigs +
                ", sensors=" + sensors +
                '}';
    }
}
