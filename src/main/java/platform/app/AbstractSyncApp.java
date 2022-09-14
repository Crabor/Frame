package platform.app;

import platform.config.CtxServerConfig;
import platform.pubsub.AbstractSubscriber;
import platform.service.ctx.CtxInteractor;
import platform.service.inv.CancerObject;
import platform.service.inv.CancerServer;

public abstract class AbstractSyncApp extends AbstractSubscriber implements SyncApp {
    protected String appName;
    private int sleepTime;
    protected int iterId;

    protected CtxInteractor ctxInteractor;

    public AbstractSyncApp() {
        appName = getClass().getName();
        iterId = 0;
        ctxInteractor = new CtxInteractor();
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
        ctxInteractor.filter(channel, msg);
        iter(channel, ctxInteractor.getMsg());
        CancerServer.iterExit(appName, iterId);
    }
}
