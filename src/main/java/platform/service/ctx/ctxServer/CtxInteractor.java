package platform.service.ctx.ctxServer;


import platform.config.Configuration;
import platform.config.SensorConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CtxInteractor {

    private final String appName;
    private final AppCtxServer ctxServer;

    private boolean ctxServerOn;
    private final Map<String, List<String>> supportedSensors;

    public CtxInteractor(boolean ctxServerOn, String appName) {
        this.appName = appName;
        this.ctxServerOn = ctxServerOn;
        this.ctxServer = new AppCtxServer(ctxServerOn, appName);
        this.supportedSensors = new HashMap<>();
        for(SensorConfig sensorConfig : Configuration.getResourceConfig().getListOfSensorObj()){
            supportedSensors.put(sensorConfig.getSensorName(), sensorConfig.getFieldNames());
        }
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
        SensorStatistics.getInstance().registerSensor(appName, sensorName);
    }

    public void cancelSensor(String sensorName){
        SensorStatistics.getInstance().cancelSensor(appName, sensorName);
    }



}