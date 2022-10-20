package platform.app;

import platform.pubsub.AbstractSubscriber;
import platform.service.ctx.ctxServer.CtxInteractor;
import platform.service.inv.CancerServer;

public abstract class AbstractApp extends AbstractSubscriber implements App{
    protected String appName;
    protected int iterId;

    protected final CtxInteractor ctxInteractor;

    public AbstractApp() {
        appName = getClass().getName();
        iterId = 0;

        //for customized ctx server
        ctxInteractor = new CtxInteractor(false, appName);
        customizeCtxServer();
        ctxInteractor.initCtxServer();
    }

    @Override
    public void onMessage(String channel, String msg) {
        iterId++;
        CancerServer.iterEntry(appName, iterId);
        iter(channel, msg);
        CancerServer.iterExit(appName, iterId);
    }

    protected abstract void customizeCtxServer();
}
