package platform.app;

import platform.config.AppConfig;
import platform.config.Configuration;
import platform.pubsub.AbstractSubscriber;
import platform.service.inv.CancerServer;

public abstract class AbstractApp extends AbstractSubscriber implements App{
    protected AppConfig config;
    protected String appName;
    protected int iterId;

    public AbstractApp() {
        appName = getClass().getName();
        iterId = 0;
        config = Configuration.getAppsConfig().get(appName);

        //for customized ctx server
        customizeCtxServer();
        config.initCtxServer();
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
