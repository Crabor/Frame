package platform.config;

import com.sun.tools.javac.Main;
import common.struct.CtxServiceConfig;
import common.struct.enumeration.CtxValidator;
import common.struct.sync.SynchronousString;
import common.util.Util;
import platform.app.AppDriver;
import platform.service.ctx.ctxServer.AppCtxServer;
import platform.service.inv.AppInvServer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AppConfig {
    private String appName;
    private int grpId;
    private Set<SensorConfig> sensors = ConcurrentHashMap.newKeySet();
    private Set<ActorConfig> actors = ConcurrentHashMap.newKeySet();
    private AppDriver appDriver = null;

    //ctxService related
    private AppCtxServer ctxServer = null;
    private boolean ctxServerOn = false;
    private String ruleFile;
    private String bfuncFile;
    private String patternFile;
    private String mfuncFile;
    private CtxValidator ctxValidator = CtxValidator.ECC_IMD;

    //invDaikon related
    private AppInvServer invServer = null;

    //request
    private Map<String, SynchronousString> requestMap = new ConcurrentHashMap<>();

    public Map<String, SynchronousString> getRequestMap() {
        return requestMap;
    }

    public void setCtxServiceConfig(CtxServiceConfig config) {
        String dir = "Resources/config/platform/ctx/" + appName;
        if (config.getRuleFileContent() != null) {
            Util.writeFileContent(dir, "rules_yellow.xml", config.getRuleFileContent(), "//huanhang");
            ruleFile = dir + "/rules_yellow.xml";
        }
        if (config.getPatternFileContent() != null) {
            Util.writeFileContent(dir, "patterns_yellow.xml", config.getPatternFileContent(), "//huanhang");
            patternFile = dir + "/patterns_yellow.xml";
        }
        if (config.getBfuncFileContent() != null) {
            Util.writeFileContent(dir, "bfuncs.java", config.getBfuncFileContent(), "//huanhang");
            String[] args = new String[] {dir + "/bfuncs.java"};
            if (Main.compile(args) == 0) {
                bfuncFile = dir + "/bfuncs.class";
            }
        }
        if (config.getMfuncFileContent() != null) {
            Util.writeFileContent(dir, "mfuncs.java", config.getMfuncFileContent(), "//huanhang");
            String[] args = new String[] {dir + "/mfuncs.java"};
            if (Main.compile(args) == 0) {
                mfuncFile = dir + "/mfuncs.class";
            }
        }
        if (config.getRfuncFileContent() != null) {
            //TODO
        }
        if (config.getCtxValidator() != null) {
            ctxValidator = config.getCtxValidator();
            ctxValidator = CtxValidator.ECC_IMD;
        }
    }

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

    public boolean addSensor(SensorConfig sensorConfig){
        if(sensors.contains(sensorConfig)){
            return false;
        }
        else{
            sensors.add(sensorConfig);
            if(ctxServer != null){
                ctxServer.getItemManager().createIndexQue(sensorConfig.getSensorName());
            }
            return true;
        }
    }

    public boolean removeSensor(SensorConfig sensorConfig){
        if(sensors.contains(sensorConfig)){
            sensors.remove(sensorConfig);
            if(ctxServer != null){
                ctxServer.getItemManager().removeIndexQue(sensorConfig.getSensorName());
            }
            return true;
        }
        else{
            return false;
        }
    }

    public Set<ActorConfig> getActors() {
        return actors;
    }

    public Set<String> getActorsName() {
        Set<String> ret = new HashSet<>();
        actors.forEach(config -> {
            ret.add(config.getActorName());
        });
        return ret;
    }

    public void addActor(ActorConfig actor) {
        this.actors.add(actor);
    }

    public void removeActor(ActorConfig actor) {
        this.actors.remove(actor);
    }

    public void registerActor(String... actors) {
        try {
            for (String actor : actors) {
                ActorConfig config = Configuration.getResourceConfig().getActorsConfig().get(actor);
                addActor(config);
                config.addApp(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelActor(String... actors) {
        try {
            for (String actor : actors) {
                ActorConfig config = Configuration.getResourceConfig().getActorsConfig().get(actor);
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

    public void setCtxValidator(CtxValidator ctxValidator) {
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

    public CtxValidator getCtxValidator() {
        return ctxValidator;
    }

    public boolean startCtxServer(){
        this.ctxServer = new AppCtxServer(this);
        this.ctxServer.init();
        for(SensorConfig sensorConfig : this.getSensors()){
            this.ctxServer.subscribe(sensorConfig.getSensorName(), grpId, 1); // apps are 0, so it should be 1.
            this.ctxServer.getItemManager().createIndexQue(sensorConfig.getSensorName());
        }
        this.ctxServer.start();
        this.ctxServerOn = true;
        return true;
    }

    public boolean resetCtxServer(){
        this.ctxServerOn = false;
        this.ctxServer.reset();
        this.ctxServerOn = true;
        return true;
    }

    public boolean stopCtxServer(){
        for(SensorConfig sensorConfig : this.getSensors()){
            this.ctxServer.unsubscribe(sensorConfig.getSensorName()); // apps are 0, so it should be 1.
            this.ctxServer.getItemManager().removeIndexQue(sensorConfig.getSensorName());
        }
        this.ctxServer.stop();
        this.ctxServerOn = false;
        return true;
    }

    //invDaikon related
    public boolean isInvServerOn() {
        //TODO
        return false;
    }

    public AppInvServer getInvServer() {
        return invServer;
    }

    public void setInvServer(AppInvServer invServer) {
        this.invServer = invServer;
    }

    @Override
    public String toString() {
        return "AppConfig{" +
                "appName='" + appName + '\'' +
                ", sensors=" + sensors +
                ", actors=" + actors +
                ", ctxServer=" + ctxServer +
                ", ctxServerOn=" + ctxServerOn +
                ", ruleFile='" + ruleFile + '\'' +
                ", bfuncFile='" + bfuncFile + '\'' +
                ", patternFile='" + patternFile + '\'' +
                ", mfuncFile='" + mfuncFile + '\'' +
                ", ctxValidator='" + ctxValidator + '\'' +
                '}';
    }

    public void setAppDriver(AppDriver appDriver) {
        this.appDriver = appDriver;
    }

    public AppDriver getAppDriver() {
        return appDriver;
    }
}
