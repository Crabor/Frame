package platform.app;

import platform.config.Configuration;
import platform.pubsub.AbstractSubscriber;
import platform.service.ctx.ctxServer.CtxInteractor;
import platform.service.inv.CancerServer;

import java.util.Set;

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

    public static void registerSensor(String appName, Set<String> sensorNames) {
        try {
            Configuration.getAppsConfig().get(appName).addSensors(sensorNames);
            sensorNames.forEach(s -> Configuration.getResourceConfig().getSensorsConfig().get(s).addApp(appName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerSensor(String... sensors) {
        registerSensor(appName, Set.of(sensors));
    }

    public static void cancelSensor(String appName, Set<String> sensorNames) {
        try {
            Configuration.getAppsConfig().get(appName).removeSensors(sensorNames);
            sensorNames.forEach(s -> Configuration.getResourceConfig().getSensorsConfig().get(s).removeApp(appName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelSensor(String... sensors) {
        cancelSensor(appName, Set.of(sensors));
    }
}
