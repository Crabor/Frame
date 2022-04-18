package platform.app;

import platform.pubsub.AbstractSubscriber;
import platform.service.inv.CancerObject;

public abstract class AbstractSyncApp extends AbstractSubscriber implements SyncApp {
    protected String appName;

    public AbstractSyncApp() {
        appName = getClass().getName();
    }

    @Override
    public void onMessage(String channel, String msg) {
        CancerObject.iterEntry(appName);
        iter(channel, msg);
    }
}
