package platform.service.ctx.ctxServer;

import com.alibaba.fastjson.JSONObject;
import platform.pubsub.AbstractSubscriber;
import platform.service.ctx.Contexts.ContextChange;
import platform.service.ctx.Contexts.Message;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

//app对应的server要设置rawdata的存储大小，base直接发给app的server
public abstract class AbstractCtxServer extends AbstractSubscriber implements Runnable{
    protected Map<String, Long> registeredSensorCounter;
    protected ChgGenerator chgGenerator;
    protected final LinkedBlockingQueue<ContextChange> changeBuffer = new LinkedBlockingQueue<>();
    protected final LinkedBlockingQueue<Message> msgBuffer = new LinkedBlockingQueue<>();

    //注册sensor
    protected abstract void initSensorCounter();
    public abstract void increaseSensorCounter(String sensorName);
    public abstract void decreaseSensorCounter(String sensorName);
    protected abstract Set<String> getRegisteredSensors();

    protected abstract JSONObject filterMessage(String msg);

    public void changeBufferProducer(List<ContextChange> changeList){
        changeBuffer.addAll(changeList);
    }

    public List<ContextChange> changeBufferConsumer(){
        List<ContextChange> changeList = new ArrayList<>();
        ContextChange change = null;
        try {
            change = changeBuffer.take();
            changeList.add(change);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        while(!changeBuffer.isEmpty()){
            change = changeBuffer.poll();
            changeList.add(change);
        }
        return changeList;
    }

    public void addMsg(long timestamp, long index, JSONObject msgObj){
        msgBuffer.offer(new Message(index, timestamp, msgObj));
    }
}
