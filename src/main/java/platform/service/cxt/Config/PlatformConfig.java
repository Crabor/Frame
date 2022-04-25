package platform.service.cxt.Config;

import com.alibaba.fastjson.JSONObject;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public class PlatformConfig {
    private static PlatformConfig INSTANCE = null;
    public static AtomicInteger context_index = new AtomicInteger();
    private boolean isCtxFixOn;
    private boolean isCtxCleanOn;
    private String CtxFixer;
    private String CtxCleaner;
    private String CtxChecker;
    private String CtxScheduler;
    private int buffer_raw_max;
    private int buffer_clean_max;
    private int delay_allowed;
    private LinkedList<String> sensorNameList;

    private static CMIDConfig CMID_CONFIG = null;

    public static LinkedList<String> changeListForChecking = new LinkedList<>();

    public static PlatformConfig getInstace(){
        return INSTANCE;
    }
    public static PlatformConfig getInstace(JSONObject object){
        INSTANCE = new PlatformConfig(object);
        CMID_CONFIG = new CMIDConfig(INSTANCE.CtxChecker, INSTANCE.CtxScheduler, INSTANCE.CtxFixer,object.getString("dataFile"), object.getString("changeHandlerType"),
                object.getString("logFilePath"), object.getString("ruleFilePath"),
                object.getString("patternFilePath"));
        return INSTANCE;
    }
    public static CMIDConfig getCMIDConfig(){
        return CMID_CONFIG;
    }
    public static LinkedList<String> getChangeListForChecking() {
        return changeListForChecking;
    }
    public LinkedList<String> getSensorNameList() {
        return sensorNameList;
    }

    PlatformConfig(JSONObject object){

        isCtxFixOn = object.getBoolean("isCtxFixOn");
        isCtxCleanOn = object.getBoolean("isCtxCleanOn");
        CtxFixer = object.getString("CtxFixer");
        CtxCleaner = object.getString("CtxCleaner");
        CtxChecker = CtxCleaner.split("\\+")[0];
        CtxScheduler = CtxCleaner.split("\\+")[1];
        buffer_raw_max = object.getIntValue("BUFFER_RAW_MAX");
        buffer_clean_max = object.getIntValue("BUFFER_CLEAN_MAX");
        sensorNameList = new LinkedList<>();
        delay_allowed = object.getIntValue("delay_allowed");
    }

    public int getDelay_allowed() {
        return delay_allowed;
    }

    public void addSensor(String sensorName){
        sensorNameList.add(sensorName);
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

    public String toString(){
        return "isCtxCleanOn = " + isCtxCleanOn
                + ", isCtxFixOn = " + isCtxFixOn
                + ", CtxCleaner = " + CtxCleaner
                + ", CtxChecker = " + CtxChecker
                + ", CtxScheduler = " + CtxScheduler
                + ", Buffer_raw_max = " + buffer_raw_max
                + ", Buffer_clean_max = " + buffer_clean_max
                + ", Delay_allowed = " + delay_allowed
                + ", CtxFixer = " + CtxFixer;
    }
}
