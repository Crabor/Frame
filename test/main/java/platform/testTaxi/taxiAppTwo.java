package platform.testTaxi;

import app.AbstractApp;
import common.struct.SensorData;

public class taxiAppTwo extends AbstractApp {
    @Override
    public void configApp() {
//        config.registerSensor("taxis");
//        config.setCtxServerOn(true);
//        config.setRuleFile("Resources/taxiTest/appTwo/rules.xml");
//        config.setBfuncFile("Resources/taxiTest/appTwo/taxiBfunction.class");
//        config.setPatternFile("Resources/taxiTest/appTwo/patterns.xml");
//        config.setMfuncFile("Resources/taxiTest/appTwo/taxiMfunction.class");
    }

    @Override
    public void getMsg(String sensorName, SensorData value) {
        logger.debug(appName + " recv " + value);
        System.out.println(appName + " recv " + value);
    }
}
