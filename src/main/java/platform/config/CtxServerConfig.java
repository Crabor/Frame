package platform.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CtxServerConfig {
    private volatile static CtxServerConfig INSTANCE = null;
    private final boolean serverOn;
    private final String ctxFixer;
    private final String ctxValidator;
    private final String ctxChecker;
    private final String ctxScheduler;
    private final String baseRuleFile;
    private final String baseBfuncFile;
    private final String basePatternFile;
    private final String baseMfuncFile;
    private final Map<String, SensorConfig> sensorConfigMap;
    private final List<SubConfig> subConfigList;


    public static CtxServerConfig getInstance(){
        return INSTANCE;
    }
    public static CtxServerConfig getInstance(JSONObject object){
        INSTANCE = new CtxServerConfig(object);
        return INSTANCE;
    }


    private CtxServerConfig(JSONObject object){
        serverOn = object.getBoolean("serverOn");
        ctxFixer = object.getString("CtxFixer");
        ctxValidator = object.getString("CtxCleaner");
        ctxChecker = ctxValidator.split("\\+")[0];
        ctxScheduler = ctxValidator.split("\\+")[1];
        baseRuleFile = object.getString("baseRuleFile");
        baseBfuncFile = object.getString("baseBfuncFile");
        basePatternFile = object.getString("basePatternFile");
        baseMfuncFile = object.getString("baseMfuncFile");
        sensorConfigMap = new HashMap<>();
        subConfigList = new ArrayList<>();
        JSONArray subs = object.getJSONArray("subscribe");
        for (int i = 0; i < subs.size(); i++) {
            JSONObject sub = subs.getJSONObject(i);
            subConfigList.add(new SubConfig(sub));
        }
    }


    public void addSensorConfig(SensorConfig sensorConfig){
        this.sensorConfigMap.put(sensorConfig.getSensorName(), sensorConfig);
    }


    public boolean isServerOn() {
        return serverOn;
    }

    public String getCtxFixer() {
        return ctxFixer;
    }

    public String getCtxValidator() {
        return ctxValidator;
    }

    public String getCtxChecker() {
        return ctxChecker;
    }

    public String getCtxScheduler() {
        return ctxScheduler;
    }

    public String getBaseRuleFile() {
        return baseRuleFile;
    }

    public String getBaseBfuncFile() {
        return baseBfuncFile;
    }

    public String getBasePatternFile() {
        return basePatternFile;
    }

    public String getBaseMfuncFile() {
        return baseMfuncFile;
    }

    public Map<String, SensorConfig> getSensorConfigMap() {
        return sensorConfigMap;
    }

    public List<SubConfig> getSubConfigList() {
        return subConfigList;
    }

    @Override
    public String toString() {
        return "CtxServerConfig{" +
                "serverOn=" + serverOn +
                ", ctxFixer='" + ctxFixer + '\'' +
                ", ctxValidator='" + ctxValidator + '\'' +
                ", ctxChecker='" + ctxChecker + '\'' +
                ", ctxScheduler='" + ctxScheduler + '\'' +
                ", baseRuleFile='" + baseRuleFile + '\'' +
                ", baseBfuncFile='" + baseBfuncFile + '\'' +
                ", basePatternFile='" + basePatternFile + '\'' +
                ", baseMfuncFile='" + baseMfuncFile + '\'' +
                ", sensorConfigMap=" + sensorConfigMap +
                ", subConfigs=" + subConfigList +
                '}';
    }
}
