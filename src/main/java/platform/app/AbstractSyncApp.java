package platform.app;

import platform.pubsub.AbstractSubscriber;

public abstract class AbstractSyncApp extends AbstractSubscriber implements SyncApp {
    @Override
    public void onMessage(String channel, String msg) {
        iter(channel, msg);
    }
}
