package platform.service.ctx.ctxServer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import platform.config.CtxServerConfig;
import platform.service.ctx.ctxChecker.CMID.builder.CheckerBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CtxBaseServer extends AbstractServer{

    private static final class CtxBaseServerHolder {
        private static final CtxBaseServer instance = new CtxBaseServer();
    }

    public static CtxBaseServer getInstance() {
        return CtxBaseServerHolder.instance;
    }

    public CtxBaseServer() {
        //init sensorCounter
        initSensorCounter();
        //init baseChecker with CMID
        Thread baseChecker = new Thread(new CheckerBuilder(CtxServerConfig.getInstace()));
        baseChecker.setPriority(Thread.MAX_PRIORITY);
        baseChecker.start();
        //init baseChecker with INFuse
//        Thread baseChecker = new Thread(new Starter(CtxServerConfig.getInstace()));
//        baseChecker.setPriority(Thread.MAX_PRIORITY);
//        baseChecker.start();

    }

    @Override
    protected void initSensorCounter(){
        assert registeredSensorCounter != null;
        synchronized (registeredSensorCounter){
            for(String sensorName : CtxServerConfig.getInstace().getSensorNameList()){
                registeredSensorCounter.put(sensorName, 0L);
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


    @Override
    public void run() {

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



}
