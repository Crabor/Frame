package platform.testTaxi;

import platform.app.AbstractApp;

public class taxiAppOne extends AbstractApp {

    @Override
    protected void customizeCtxServer() {
        config.registerSensor("taxis");
        config.registerSensor("front");
        config.registerSensor("back");
        config.setCtxServerOn(true);
        config.setRuleFile("Resources/taxiTest/appOne/taxiRules.xml");
        config.setBfuncFile("Resources/taxiTest/appOne/taxiBfunction.class");
        config.setPatternFile("Resources/taxiTest/appOne/taxiPatterns.xml");
        config.setMfuncFile("Resources/taxiTest/appOne/taxiMfunction.class");
    }

    @Override
    public void iter(String channel, String msg) {
        System.out.println(appName + " recv " + msg);
    }
}
