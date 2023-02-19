package platform.testTaxi;

import app.AbstractApp;
import common.struct.SensorData;
import common.struct.enumeration.CmdType;
import common.struct.enumeration.ServiceType;

public class taxiAppOne extends AbstractApp {

    @Override
    public void setting() {
//        config.registerSensor("taxis");
//        config.setCtxServerOn(true);
//        config.setRuleFile("Resources/taxiTest/appOne/taxiRules.xml");
//        config.setBfuncFile("Resources/taxiTest/appOne/taxiBfunction.class");
//        config.setPatternFile("Resources/taxiTest/appOne/taxiPatterns.xml");
//        config.setMfuncFile("Resources/taxiTest/appOne/taxiMfunction.class");
    }

    @Override
    public void getMsg(String sensorName, SensorData value) {
        logger.debug(appName + " recv " + value);
        System.out.println(appName + " recv " + value);
//        if(value.contains("2011-04-08-04:00:00:000")){
//            call(ServiceType.CTX, CmdType.RESET);
//        }
    }
}
