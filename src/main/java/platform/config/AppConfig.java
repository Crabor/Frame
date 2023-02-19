package platform.config;

import platform.service.ctx.ctxServer.AppCtxServer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AppConfig {
    private String appName;
    private int grpId;
    private List<SubConfig> subConfigs = new ArrayList<>();
    private Set<SensorConfig> sensors = ConcurrentHashMap.newKeySet();
    private Set<ActorConfig> actuators = ConcurrentHashMap.newKeySet();

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

    public int getGrpId() {
        return grpId;
    }

    public void setGrpId(int grpId) {
        this.grpId = grpId;
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

    public Set<ActorConfig> getActors() {
        return actuators;
    }

    public Set<String> getActorsName() {
        Set<String> ret = new HashSet<>();
        actuators.forEach(config -> {
            ret.add(config.getActorName());
        });
        return ret;
    }

    public void addActor(ActorConfig actuator) {
        this.actuators.add(actuator);
    }

    public void removeActor(ActorConfig actuator) {
        this.actuators.remove(actuator);
    }

    public void registerActor(String... actuators) {
        try {
            for (String actuator : actuators) {
                ActorConfig config = Configuration.getResourceConfig().getActorsConfig().get(actuator);
                addActor(config);
                config.addApp(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelActor(String... actuators) {
        try {
            for (String actuator : actuators) {
                ActorConfig config = Configuration.getResourceConfig().getActorsConfig().get(actuator);
                removeActor(config);
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
