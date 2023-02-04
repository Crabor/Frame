package platform.config;

import platform.service.ctx.ctxServer.AppCtxServer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AppConfig {
    private String appName;
    private List<SubConfig> subConfigs = new ArrayList<>();
    private Set<SensorConfig> sensors = ConcurrentHashMap.newKeySet();
    private Set<ActuatorConfig> actuators = ConcurrentHashMap.newKeySet();

    //ctxService related
    private AppCtxServer ctxServer;
    private boolean ctxServerOn;
    private String ruleFile;
    private String bfuncFile;
    private String patternFile;
    private String mfuncFile;
    private String ctxValidator = "ECC+IMD";

//    public AppConfig(JSONObject object) {
//        this.appName = object.getString("appName");
//        JSONArray subs = object.getJSONArray("subscribe");
//        for (int i = 0; i < subs.size(); i++) {
//            JSONObject sub = subs.getJSONObject(i);
//            subConfigs.add(new SubConfig(sub));
//        }
//        try {
//            JSONArray rss = object.getJSONArray("registerSensors");
//            for (int i = 0; i < rss.size(); i++) {
//                String rs = rss.getString(i);
//                registerSensor(rs);
//            }
//        } catch (Exception e) {
//
//        }
//    }

    public AppConfig(String appName) {
        this.appName = appName;
    }

    public String getAppName() {
        return appName;
    }

    public List<SubConfig> getSubConfigs() {
        return subConfigs;
    }

    public Set<SensorConfig> getSensors() {
        return sensors;
    }

    public Set<String> getSensorsName() {
        Set<String> ret = new HashSet<>();
        sensors.forEach(config -> {
            ret.add(config.getSensorName());
        });
        return ret;
    }

    public void addSensor(SensorConfig sensor) {
        this.sensors.add(sensor);
    }

    public void removeSensor(SensorConfig sensor) {
        this.sensors.remove(sensor);
    }

    public void registerSensor(String... sensors) {
        try {
            for (String sensor : sensors) {
                SensorConfig config = Configuration.getResourceConfig().getSensorsConfig().get(sensor);
                addSensor(config);
                config.addApp(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelSensor(String... sensors) {
        try {
            for (String sensor : sensors) {
                SensorConfig config = Configuration.getResourceConfig().getSensorsConfig().get(sensor);
                removeSensor(config);
                config.removeApp(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Set<ActuatorConfig> getActuators() {
        return actuators;
    }

    public Set<String> getActuatorsName() {
        Set<String> ret = new HashSet<>();
        actuators.forEach(config -> {
            ret.add(config.getActuatorName());
        });
        return ret;
    }

    public void addActuator(ActuatorConfig actuator) {
        this.actuators.add(actuator);
    }

    public void removeActuator(ActuatorConfig actuator) {
        this.actuators.remove(actuator);
    }

    public void registerActuator(String... actuators) {
        try {
            for (String actuator : actuators) {
                ActuatorConfig config = Configuration.getResourceConfig().getActuatorsConfig().get(actuator);
                addActuator(config);
                config.addApp(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelActuator(String... actuators) {
        try {
            for (String actuator : actuators) {
                ActuatorConfig config = Configuration.getResourceConfig().getActuatorsConfig().get(actuator);
                removeActuator(config);
                config.removeApp(this);
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
        // TODO:ctxServer转发给AppDriver
        for(SubConfig subConfig : this.getSubConfigs()){
            this.ctxServer.subscribe(subConfig.channel, subConfig.groupId, subConfig.priorityId + 1);
        }
        this.ctxServer.start();
    }

    public void resetCtxServer(){
        this.ctxServer.reset();
    }

    @Override
    public String toString() {
        return "AppConfig{" +
                "appName='" + appName + '\'' +
                ", sensors=" + sensors +
                ", actuators=" + actuators +
                ", ctxServer=" + ctxServer +
                ", ctxServerOn=" + ctxServerOn +
                ", ruleFile='" + ruleFile + '\'' +
                ", bfuncFile='" + bfuncFile + '\'' +
                ", patternFile='" + patternFile + '\'' +
                ", mfuncFile='" + mfuncFile + '\'' +
                ", ctxValidator='" + ctxValidator + '\'' +
                '}';
    }
}
