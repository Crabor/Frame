package platform.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AppConfig {
    private String appName;
    private int sleepTime; //ms
    private List<SubConfig> subConfigs = new ArrayList<>();

    public AppConfig(JSONObject object) {
        this.appName = object.getString("appName");
        this.sleepTime = object.getInteger("sleepTime");
        JSONArray subs = object.getJSONArray("subscribe");
        for (int i = 0; i < subs.size(); i++) {
            JSONObject sub = subs.getJSONObject(i);
            subConfigs.add(new SubConfig(sub));
        }
    }

    public String getAppName() {
        return appName;
    }

    public int getSleepTime() {
        return sleepTime;
    }

    public List<SubConfig> getSubConfigs() {
        return subConfigs;
    }

    @Override
    public String toString() {
        return "AppConfig{" +
                "appName='" + appName + '\'' +
                ", sleepTime=" + sleepTime +
                ", subConfigs=" + subConfigs +
                '}';
    }
}
