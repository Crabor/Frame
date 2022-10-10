package platform.service.ctx.ctxServer;

import java.util.HashMap;

public class AppCtxServer extends AbstractCtxServer{

    private final boolean ctxServerOn;

    public AppCtxServer(boolean ctxServerOn, String appName) {
        this.ctxServerOn = ctxServerOn;
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
