package platform.testTaxi;

import platform.app.AbstractSyncApp;

public class taxiSyncAppOne extends AbstractSyncApp {

    @Override
    protected void customizeCtxServer() {
        ctxInteractor.registerSensor("taxis");
        ctxInteractor.registerSensor("front");
        ctxInteractor.registerSensor("back");
        ctxInteractor.setCtxServerOn(true);
        ctxInteractor.setRuleFile("Resources/taxiTest/appOne/taxiRules.xml");
        ctxInteractor.setBfuncFile("Resources/taxiTest/appOne/taxiBfunction.class");
        ctxInteractor.setPatternFile("Resources/taxiTest/appOne/taxiPatterns.xml");
        ctxInteractor.setMfuncFile("Resources/taxiTest/appOne/taxiMfunction.class");
    }

    @Override
    public void iter(String channel, String msg) {
        System.out.println(appName + " recv " + msg);
    }
}
