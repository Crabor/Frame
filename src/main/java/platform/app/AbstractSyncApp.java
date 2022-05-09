package platform.app;

import platform.pubsub.AbstractSubscriber;
import platform.service.inv.CancerObject;
import platform.service.inv.CancerServer;

public abstract class AbstractSyncApp extends AbstractSubscriber implements SyncApp {
    protected String appName;
    private int sleepTime;
    protected int iterId;

    public AbstractSyncApp() {
        appName = getClass().getSimpleName();
        iterId = 0;
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    @Override
    public void onMessage(String channel, String msg) {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        iterId++;
        CancerServer.iterEntry(appName, iterId);
        iter(channel, msg);
        CancerServer.iterExit(appName, iterId);
    }
}
