package platform.config;

import com.sun.tools.javac.Main;
import common.struct.CtxServiceConfig;
import common.struct.enumeration.CtxValidator;
import common.util.Util;
import platform.service.ctx.ctxServer.AppCtxServer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AppConfig {
    private String appName;
    private int grpId;
    private Set<SensorConfig> sensors = ConcurrentHashMap.newKeySet();
    private Set<ActorConfig> actors = ConcurrentHashMap.newKeySet();

    //ctxService related
    private AppCtxServer ctxServer;
    private boolean ctxServerOn;
    private String ruleFile;
    private String bfuncFile;
    private String patternFile;
    private String mfuncFile;
    private CtxValidator ctxValidator = CtxValidator.ECC_IMD;

    public void setCtxServiceConfig(CtxServiceConfig config) {
        String dir = "Resources/configFile/ctxFile/" + appName;
        if (config.getRuleFileContent() != null) {
            Util.writeFileContent(dir, "rules.xml", config.getRuleFileContent());
            ruleFile = dir + "/rules.xml";
        } else if (config.getPatternFileContent() != null) {
            Util.writeFileContent(dir, "patterns.xml", config.getPatternFileContent());
            patternFile = dir + "/patterns.xml";
        } else if (config.getBfuncFileContent() != null) {
            Util.writeFileContent(dir, "bfuncs.java", config.getBfuncFileContent());
            String[] args = new String[] {dir + "/bfuncs.java"};
            if (Main.compile(args) == 0) {
                bfuncFile = dir + "/bfuncs.class";
            }
        } else if (config.getMfuncFileContent() != null) {
            Util.writeFileContent(dir, "mfuncs.java", config.getMfuncFileContent());
            String[] args = new String[] {dir + "/mfuncs.java"};
            if (Main.compile(args) == 0) {
                mfuncFile = dir + "/mfuncs.class";
            }
        } else if (config.getRfuncFileContent() != null) {
            //TODO
        } else if (config.getCtxValidator() != null) {
            ctxValidator = config.getCtxValidator();
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

    public void initCtxServer(){
        this.ctxServer = new AppCtxServer(this);
        this.ctxServer.init();
        //subscribe
        for(SensorConfig sensorConfig : this.getSensors()){
            this.ctxServer.subscribe(sensorConfig.getSensorName(), grpId, 1); // current 1
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
}
