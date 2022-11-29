package platform.testTaxi;

import platform.app.AbstractApp;

public class taxiAppTwo extends AbstractApp {
    @Override
    protected void customizeCtxServer() {
        config.registerSensor("taxis");
        config.setCtxServerOn(true);
        config.setRuleFile("Resources/taxiTest/appTwo/taxiRules.xml");
        config.setBfuncFile("Resources/taxiTest/appTwo/taxiBfunction.class");
        config.setPatternFile("Resources/taxiTest/appTwo/taxiPatterns.xml");
        config.setMfuncFile("Resources/taxiTest/appTwo/taxiMfunction.class");
    }

    @Override
    public void iter(String channel, String msg) {
        logger.debug(appName + " recv " + msg);
        System.out.println(appName + " recv " + msg);
    }
}
