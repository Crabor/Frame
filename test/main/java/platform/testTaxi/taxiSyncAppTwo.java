package platform.testTaxi;

import platform.app.AbstractApp;

public class taxiSyncAppTwo extends AbstractApp {
    @Override
    protected void customizeCtxServer() {
        ctxInteractor.registerSensor("taxis");
        ctxInteractor.registerSensor("left");
        ctxInteractor.registerSensor("right");
        ctxInteractor.setCtxServerOn(true);
        ctxInteractor.setRuleFile("Resources/taxiTest/appTwo/taxiRules.xml");
        ctxInteractor.setBfuncFile("Resources/taxiTest/appTwo/taxiBfunction.class");
        ctxInteractor.setPatternFile("Resources/taxiTest/appTwo/taxiPatterns.xml");
        ctxInteractor.setMfuncFile("Resources/taxiTest/appTwo/taxiMfunction.class");
    }

    @Override
    public void iter(String channel, String msg) {
        System.out.println(appName + " recv " + msg);
    }
}
