package platform.app;

import platform.config.AppConfig;
import platform.config.Configuration;
import platform.comm.pubsub.AbstractSubscriber;
import platform.service.inv.CancerServer;

public abstract class AbstractApp extends AbstractSubscriber implements App, Runnable {
    Thread t;
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
        boolean invSeverOn = Configuration.getInvServerConfig().isServerOn();
        if (invSeverOn) CancerServer.iterEntry(appName, iterId, msg);
        iter(channel, msg);
        if (invSeverOn) CancerServer.iterExit(appName, iterId);
    }

    @Override
    public void run() {}

    public void start() {
        if (t == null) {
            t = new Thread(this, getClass().getName());
            t.start();
        }
    }

    protected abstract void customizeCtxServer();
}
