package platform.service.cxt.Config;

import com.alibaba.fastjson.JSONObject;

public class AppConfig {
    private String appName;
    private int sleepTime; //ms

    public AppConfig(JSONObject object) {
        this.appName = object.getString("appName");
        this.sleepTime = object.getInteger("sleepTime");
    }

    public String getAppName() {
        return appName;
    }

    public int getSleepTime() {
        return sleepTime;
    }

    @Override
    public String toString() {
        return "AppConfig{" +
                "appName='" + appName + '\'' +
                ", sleepTime=" + sleepTime +
                '}';
    }
}
