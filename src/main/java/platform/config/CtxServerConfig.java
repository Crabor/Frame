package platform.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CtxServerConfig {
    private volatile static CtxServerConfig INSTANCE = null;
    public static AtomicInteger ctxIndex = new AtomicInteger();
    private boolean serverOn;
    private String CtxFixer;
    private String CtxCleaner;
    private String CtxChecker;
    private String CtxScheduler;
    private int buffer_raw_max;
    private int delay_allowed;
    private List<SensorConfig> sensorConfigList;
    private LinkedList<SubConfig> subConfigs = new LinkedList<>();
    private static INFuseConfig inFuseConfig = null;


    public static CtxServerConfig getInstace(){
        return INSTANCE;
    }
    public static CtxServerConfig getInstace(JSONObject object){
        INSTANCE = new CtxServerConfig(object);
        inFuseConfig = new INFuseConfig(object.getString("baseRuleFile"), object.getString("baseBfuncFile"),
                object.getString("basePatternFile"), object.getString("baseMfuncFile"), INSTANCE.CtxFixer, INSTANCE.CtxCleaner);
        return INSTANCE;
    }

    public static INFuseConfig getInFuseConfig() {
        return inFuseConfig;
    }
    public List<SensorConfig> getSensorConfigList() {return sensorConfigList;}

    private CtxServerConfig(JSONObject object){
        serverOn = object.getBoolean("serverOn");
        CtxFixer = object.getString("CtxFixer");
        CtxCleaner = object.getString("CtxCleaner");
        CtxChecker = CtxCleaner.split("\\+")[0];
        CtxScheduler = CtxCleaner.split("\\+")[1];
        buffer_raw_max = object.getIntValue("BUFFER_RAW_MAX");
        sensorConfigList = new ArrayList<>();
        delay_allowed = object.getIntValue("delay_allowed");
        JSONArray subs = object.getJSONArray("subscribe");
        for (int i = 0; i < subs.size(); i++) {
            JSONObject sub = subs.getJSONObject(i);
            subConfigs.add(new SubConfig(sub));
        }
    }

    public boolean isServerOn() {
        return serverOn;
    }
    public int getDelay_allowed() {
        return delay_allowed;
    }

    public void addSensorConfig(SensorConfig sensorConfig){
        sensorConfigList.add(sensorConfig);
    }
    public boolean isCtxFixOn() {
        return isCtxFixOn;
    }

    public void setCtxFixOn(boolean ctxFixOn) {
        isCtxFixOn = ctxFixOn;
    }

    public boolean isCtxCleanOn() {
        return isCtxCleanOn;
    }

    public void setCtxCleanOn(boolean ctxCleanOn) {
        isCtxCleanOn = ctxCleanOn;
    }

    public String getCtxFixer() {
        return CtxFixer;
    }

    public void setCtxFixer(String ctxFixer) {
        CtxFixer = ctxFixer;
    }

    public String getCtxCleaner() {
        return CtxCleaner;
    }

    public void setCtxCleaner(String ctxCleaner) {
        CtxCleaner = ctxCleaner;
    }

    public String getCtxChecker() {
        return CtxChecker;
    }

    public void setCtxChecker(String ctxChecker) {
        CtxChecker = ctxChecker;
    }

    public String getCtxScheduler() {
        return CtxScheduler;
    }

    public void setCtxScheduler(String ctxScheduler) {
        CtxScheduler = ctxScheduler;
    }

    public int getBuffer_raw_max() {
        return buffer_raw_max;
    }

    public void setBuffer_raw_max(int buffer_max) {
        this.buffer_raw_max = buffer_max;
    }

    public int getBuffer_clean_max() {
        return buffer_clean_max;
    }

    public void setBuffer_clean_max(int buffer_clean_max) {
        this.buffer_clean_max = buffer_clean_max;
    }

    public LinkedList<SubConfig> getSubConfigs() {
        return subConfigs;
    }

    @Override
    public String toString() {
        return "CtxServerConfig{" +
                "serverOn=" + serverOn +
                ", isCtxFixOn=" + isCtxFixOn +
                ", isCtxCleanOn=" + isCtxCleanOn +
                ", CtxFixer='" + CtxFixer + '\'' +
                ", CtxCleaner='" + CtxCleaner + '\'' +
                ", CtxChecker='" + CtxChecker + '\'' +
                ", CtxScheduler='" + CtxScheduler + '\'' +
                ", buffer_raw_max=" + buffer_raw_max +
                ", buffer_clean_max=" + buffer_clean_max +
                ", delay_allowed=" + delay_allowed +
                ", sensorConfigList=" + sensorConfigList +
                ", subConfigs=" + subConfigs +
                '}';
    }
}
