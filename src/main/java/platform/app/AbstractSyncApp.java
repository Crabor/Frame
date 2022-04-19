package platform.app;

import platform.pubsub.AbstractSubscriber;
import platform.service.inv.CancerObject;
import platform.service.inv.CancerServer;

public abstract class AbstractSyncApp extends AbstractSubscriber implements SyncApp {
    protected String appName;
    protected int iterId;

    public AbstractSyncApp() {
        appName = getClass().getName();
        iterId = 0;
    }

    @Override
    public void onMessage(String channel, String msg) {
        iterId++;
        CancerServer.iterEntry(appName, iterId);
        iter(channel, msg);
        CancerServer.iterExit(appName, iterId);
    }
}
