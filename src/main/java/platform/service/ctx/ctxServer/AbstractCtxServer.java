package platform.service.ctx.ctxServer;

import platform.pubsub.AbstractSubscriber;
import platform.service.ctx.Contexts.ContextChange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//app对应的server要设置rawdata的存储大小，base直接发给app的server
public abstract class AbstractCtxServer extends AbstractSubscriber implements Runnable{
    protected final Map<String, Long> registeredSensorCounter = new HashMap<>();
    protected ChgGenerator chgGenerator;
    protected final List<ContextChange> changeBuffer = new ArrayList<>();

    //注册sensor
    protected abstract void initSensorCounter();
    public abstract void increaseSensorCounter(String sensorName);
    public abstract void decreaseSensorCounter(String sensorName);

    protected abstract void filterMessage();

    public void changeBufferProducer(List<ContextChange> changeList){
        synchronized (changeBuffer){
            changeBuffer.addAll(changeList);
        }
    }

    public List<ContextChange> changeBufferConsumer(){
        List<ContextChange> changeList;
        synchronized (changeBuffer){
            changeList = new ArrayList<>(changeBuffer);
            changeBuffer.clear();
        }
        return changeList;
    }
}
