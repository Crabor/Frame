package platform.app;

import platform.pubsub.AbstractSubscriber;
import platform.service.ctx.ctxServer.CtxInteractor;
import platform.service.inv.CancerServer;

public abstract class AbstractSyncApp extends AbstractSubscriber implements SyncApp {
    protected String appName;
    private int sleepTime;
    protected int iterId;

    protected final CtxInteractor ctxInteractor;

    public AbstractSyncApp() {
        appName = getClass().getName();
        iterId = 0;

        //for customized ctx server
        ctxInteractor = new CtxInteractor(false, appName);
        customizeCtxServer();
        ctxInteractor.initCtxServer();
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

    protected abstract void customizeCtxServer();
}
