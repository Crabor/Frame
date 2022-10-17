package platform.app;

import platform.pubsub.AbstractSubscriber;
import platform.service.ctx.CtxInteractor;
import platform.service.inv.CancerServer;

public abstract class AbstractApp extends AbstractSubscriber implements App{
    protected String appName;
    protected int iterId;

    protected CtxInteractor ctxInteractor;

    public AbstractApp() {
        appName = getClass().getName();
        iterId = 0;
        ctxInteractor = new CtxInteractor();
    }

    @Override
    public void onMessage(String channel, String msg) {
        iterId++;
        CancerServer.iterEntry(appName, iterId);
        ctxInteractor.filter(channel, msg);
        iter(channel, ctxInteractor.getMsg());
        CancerServer.iterExit(appName, iterId);
    }
}
