package platform.testTaxi;

import app.AbstractApp;

public class taxiAppTwo extends AbstractApp {
    @Override
    public void setting() {
//        config.registerSensor("taxis");
//        config.setCtxServerOn(true);
//        config.setRuleFile("Resources/taxiTest/appTwo/taxiRules.xml");
//        config.setBfuncFile("Resources/taxiTest/appTwo/taxiBfunction.class");
//        config.setPatternFile("Resources/taxiTest/appTwo/taxiPatterns.xml");
//        config.setMfuncFile("Resources/taxiTest/appTwo/taxiMfunction.class");
    }

    @Override
    public void getMsg(String sensorName, String value) {
        logger.debug(appName + " recv " + value);
        System.out.println(appName + " recv " + value);
    }
}
