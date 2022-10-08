package platform.service.ctx.ctxServer;

import platform.app.App;
import platform.service.ctx.ctxChecker.context.Context;
import platform.service.ctx.sensorStatitic.AppSensorStatistics;

import java.util.HashMap;

public class AppCtxServer extends AbstractCtxServer{

    private final boolean ctxServerOn;

    public AppCtxServer(boolean ctxServerOn, String appName) {
        this.ctxServerOn = ctxServerOn;
        this.sensorStatistics = new AppSensorStatistics(appName);
        this.patternMap = new HashMap<>();
        this.ruleMap = new HashMap<>();
        this.resolverMap = new HashMap<>();
    }

    @Override
    public void init() {

    }

    @Override
    public void onMessage(String channel, String msg) {

    }

    @Override
    public void run() {

    }

}
