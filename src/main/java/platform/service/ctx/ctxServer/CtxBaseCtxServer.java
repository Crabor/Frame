package platform.service.ctx.ctxServer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import platform.config.CtxServerConfig;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CtxBaseCtxServer extends AbstractCtxServer {
    private Thread t;

    private static final class CtxBaseServerHolder {
        private static final CtxBaseCtxServer instance = new CtxBaseCtxServer();
    }

    public static CtxBaseCtxServer getInstance() {
        return CtxBaseServerHolder.instance;
    }

    public CtxBaseCtxServer(){
        this.chgGenerator = new ChgGenerator(this);

    }






    public CtxBaseCtxServer() {
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

    public void start() {
        if (t == null) {
            t = new Thread(this, getClass().getName());
            t.start();
        }
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
