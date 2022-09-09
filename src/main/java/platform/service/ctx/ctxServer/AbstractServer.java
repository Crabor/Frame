package platform.service.ctx.ctxServer;

import platform.pubsub.AbstractSubscriber;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractServer extends AbstractSubscriber implements Runnable{
    protected final Map<String, Long> registeredSensorCounter = new HashMap<>();
    protected abstract void initSensorCounter();
    public abstract void increaseSensorCounter(String sensorName);
    public abstract void decreaseSensorCounter(String sensorName);
    protected abstract void filterMessage();



}
