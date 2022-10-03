package platform.service.ctx.ctxServer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import platform.config.Configuration;
import platform.config.CtxServerConfig;
import platform.service.ctx.Contexts.ContextChange;
import platform.service.ctx.Messages.Message;
import platform.service.ctx.Messages.MessageBuilder;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CtxBaseCtxServer extends AbstractCtxServer {
    private Thread t;

    private static final class CtxBaseServerHolder {
        private static final CtxBaseCtxServer instance = new CtxBaseCtxServer();
    }

    public static CtxBaseCtxServer getInstance() {
        return CtxBaseServerHolder.instance;
    }

    @Override
    public void init() {
        initSensorCounter();
        increaseSensorCounter("taxis");
        if(Configuration.getCtxServerConfig().isServerOn()){
            this.chgGenerator = new ChgGenerator(CtxBaseCtxServer.getInstance());
            this.patternMap = this.chgGenerator.buildPatterns(CtxServerConfig.getInstance().getBasePatternFile(), CtxServerConfig.getInstance().getBaseMfuncFile());
            //Thread baseChecker = new Thread(new Starter());

            this.chgGenerator.start();
        }
    }

    @Override
    protected void initSensorCounter(){
        this.registeredSensorCounter = new ConcurrentHashMap<>();
        for(String sensorName : CtxServerConfig.getInstance().getSensorConfigMap().keySet()){
            registeredSensorCounter.put(sensorName, 0L);
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

    @Override
    public void onMessage(String channel, String msg) {
        logger.debug("ctx recv: " + msg);
        JSONObject jsonObject = filterMessage(msg);
        if(jsonObject.keySet().isEmpty()) {
            return;
        }

        Message message = MessageBuilder.jsonObject2Message(jsonObject);
        addMsg(message);

        if(CtxServerConfig.getInstance().isServerOn()){
            chgGenerator.generateChanges(message);
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
