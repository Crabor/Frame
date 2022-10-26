package platform.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import platform.app.AbstractApp;
import platform.service.ctx.ctxServer.AppCtxServer;

import java.util.*;

public class AppConfig {
    private String appName;
    private List<SubConfig> subConfigs = new ArrayList<>();
    private Set<String> sensors = new HashSet<>();

    //ctxService related
    private AppCtxServer ctxServer;
    private boolean ctxServerOn;
    private String ruleFile;
    private String bfuncFile;
    private String patternFile;
    private String mfuncFile;
    private String ctxValidator = "ECC+IMD";

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


    //ctxService related
    public void setCtxServerOn(boolean ctxServerOn) {
        this.ctxServerOn = ctxServerOn;
    }

    public void setRuleFile(String ruleFile){
        this.ruleFile = ruleFile;
    }

    public void setBfuncFile(String bfuncFile) {
        this.bfuncFile = bfuncFile;
    }

    public void setPatternFile(String patternFile) {
        this.patternFile = patternFile;
    }

    public void setMfuncFile(String mfuncFile) {
        this.mfuncFile = mfuncFile;
    }

    public void setCtxValidator(String ctxValidator) {
        this.ctxValidator = ctxValidator;
    }

    public AppCtxServer getCtxServer() {
        return ctxServer;
    }

    public boolean isCtxServerOn() {
        return ctxServerOn;
    }

    public String getRuleFile() {
        return ruleFile;
    }

    public String getBfuncFile() {
        return bfuncFile;
    }

    public String getPatternFile() {
        return patternFile;
    }

    public String getMfuncFile() {
        return mfuncFile;
    }

    public String getCtxValidator() {
        return ctxValidator;
    }

    public void initCtxServer(){
        this.ctxServer = new AppCtxServer(this);
        this.ctxServer.init();
        //subscribe
        for(SubConfig subConfig : this.getSubConfigs()){
            this.ctxServer.subscribe(subConfig.channel, subConfig.groupId, subConfig.priorityId + 1);
        }
        this.ctxServer.start();
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
