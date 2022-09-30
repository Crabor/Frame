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
import java.util.concurrent.atomic.AtomicInteger;

public class CtxBaseCtxServer extends AbstractCtxServer {
    private Thread t;
    private HashMap<String, Pattern> patternMap;
    public static AtomicInteger ctxIndex = new AtomicInteger();

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
        assert registeredSensorCounter != null;
        synchronized (registeredSensorCounter){
            for(SensorConfig sensorConfig : CtxServerConfig.getInstance().getSensorConfigList()){
                registeredSensorCounter.put(sensorConfig.getSensorName(), 0L);
            }
        }
    }

    @Override
    public void increaseSensorCounter(String sensorName){
        assert registeredSensorCounter != null;
        synchronized (registeredSensorCounter){
            registeredSensorCounter.computeIfPresent(sensorName, (k,v) -> v+1);
        }
    }

    @Override
    public void decreaseSensorCounter(String sensorName){
        assert  registeredSensorCounter != null;
        synchronized (registeredSensorCounter){
            registeredSensorCounter.computeIfPresent(sensorName, (k,v) -> v-1);
        }
    }

    @Override
    protected void filterMessage() {

    }


    public HashMap<String, Pattern> getPatternMap() {
        return patternMap;
    }

    @Override
    public void onMessage(String channel, String msg) {
        logger.debug("ctx recv: " + msg);
        JSONObject jo = JSON.parseObject(msg);

        int index = CtxServerConfig.ctxIndex.getAndIncrement();
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        //
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
