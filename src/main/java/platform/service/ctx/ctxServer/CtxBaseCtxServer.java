package platform.service.ctx.ctxServer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import platform.config.Configuration;
import platform.config.CtxServerConfig;
import platform.config.SensorConfig;
import platform.service.ctx.Patterns.Pattern;
import platform.service.ctx.ctxChecker.INFuse.Starter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CtxBaseCtxServer extends AbstractCtxServer {
    private Thread t;
    private HashMap<String, Pattern> patternMap;
    public static AtomicLong msgIndex=  new AtomicLong();

    private static final class CtxBaseServerHolder {
        private static final CtxBaseCtxServer instance = new CtxBaseCtxServer();
    }

    public static CtxBaseCtxServer getInstance() {
        return CtxBaseServerHolder.instance;
    }

    public CtxBaseCtxServer(){
        initSensorCounter();
        if(Configuration.getCtxServerConfig().isServerOn()){
            this.chgGenerator = new ChgGenerator(CtxBaseCtxServer.getInstance());
            this.patternMap = this.chgGenerator.buildPatterns(CtxServerConfig.getInstance().getBasePatternFile());
            //Thread baseChecker = new Thread(new Starter());
        }
    }

    @Override
    protected void initSensorCounter(){
        this.registeredSensorCounter = new ConcurrentHashMap<>();
        for(SensorConfig sensorConfig : CtxServerConfig.getInstance().getSensorConfigList()){
            registeredSensorCounter.put(sensorConfig.getSensorName(), 0L);
        }
    }

    @Override
    public void increaseSensorCounter(String sensorName){
        assert registeredSensorCounter != null;
        registeredSensorCounter.computeIfPresent(sensorName, (k,v) -> v + 1);
    }

    @Override
    public void decreaseSensorCounter(String sensorName){
        assert  registeredSensorCounter != null;
        registeredSensorCounter.computeIfPresent(sensorName, (k,v) -> v - 1 > 0 ? v - 1 : 0);
    }

    @Override
    protected Set<String> getRegisteredSensors() {
        Set<String> registeredSensors = new HashSet<>();
        for(String sensorName : registeredSensorCounter.keySet()){
            if(registeredSensorCounter.get(sensorName) > 0) {
                registeredSensors.add(sensorName);
            }
        }
        return registeredSensors;
    }

    @Override
    protected JSONObject filterMessage(String msg) {
        JSONObject jsonObject = JSON.parseObject(msg);
        Set<String> registeredSensors = getRegisteredSensors();
        for(String msgSensor : jsonObject.keySet()){
            if(!registeredSensors.contains(msgSensor)){
                jsonObject.remove(msgSensor);
            }
        }
        return jsonObject;
    }

    public HashMap<String, Pattern> getPatternMap() {
        return patternMap;
    }

    @Override
    public void onMessage(String channel, String msg) {
        logger.debug("ctx recv: " + msg);
        JSONObject jsonObject = filterMessage(msg);
        if(jsonObject.keySet().isEmpty()) {
            return;
        }

        long timestamp = new Date().getTime();
        long index = msgIndex.getAndIncrement();
        addMsg(timestamp, index, jsonObject);
        if(CtxServerConfig.getInstance().isServerOn()){
            chgGenerator.generateChanges(jsonObject);
            //TODO()
        }
        else{
            //TODO()
        }
    }


    @Override
    public void run() {

    }

    public void start() {
        if (t == null) {
            t = new Thread(this, getClass().getName());
            t.start();
        }
    }



}
