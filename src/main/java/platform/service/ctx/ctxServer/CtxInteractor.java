package platform.service.ctx.ctxServer;


import platform.config.AppConfig;
import platform.config.Configuration;
import platform.config.SensorConfig;
import platform.config.SubConfig;
import platform.service.ctx.statistics.SensorStatistics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CtxInteractor {

    private final static Map<String, List<String>> supportedSensors = new HashMap<>();

    static {
        for(SensorConfig sensorConfig : Configuration.getResourceConfig().getSensorsConfig().values()){
            supportedSensors.put(sensorConfig.getSensorName(), sensorConfig.getFieldNames());
        }
    }

    private AppConfig appConfig;
    private AppCtxServer ctxServer;
    private boolean ctxServerOn;
    private String ruleFile;
    private String bfuncFile;
    private String patternFile;
    private String mfuncFile;
    private String ctxValidator = "ECC+IMD";


    public CtxInteractor(boolean ctxServerOn, String appName) {
        for(AppConfig tmpConfig : Configuration.getAppsConfig().values()){
            if(tmpConfig.getAppName().equals(appName)){
                this.appConfig = tmpConfig;
                break;
            }
        }
        this.ctxServerOn = ctxServerOn;
    }

    public boolean isCtxServerOn() {
        return ctxServerOn;
    }
    public void setCtxServerOn(boolean ctxServerOn) {
        this.ctxServerOn = ctxServerOn;
    }

    //sensor related
    public Map<String, List<String>> getSupportedSensors(){
        return supportedSensors;
    }
    public void registerSensor(String sensorName){
        SensorStatistics.getInstance().registerSensor(appConfig.getAppName(), sensorName);
    }
    public void cancelSensor(String sensorName){
        SensorStatistics.getInstance().cancelSensor(appConfig.getAppName(), sensorName);
    }

    //Configuration
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

    public AppConfig getAppConfig() {
        return appConfig;
    }

    public AppCtxServer getCtxServer() {
        return ctxServer;
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
        for(SubConfig subConfig : appConfig.getSubConfigs()){
            this.ctxServer.subscribe(subConfig.channel, subConfig.groupId, subConfig.priorityId + 1);
        }
        this.ctxServer.start();
    }
}