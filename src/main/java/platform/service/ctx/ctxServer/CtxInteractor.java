package platform.service.ctx.ctxServer;


public class CtxInteractor {

    private final String appName;
    private final AppCtxServer ctxServer;

    public CtxInteractor(boolean ctxServerOn, String appName) {
        this.appName = appName;
        this.ctxServer = new AppCtxServer(ctxServerOn, appName);
    }



}