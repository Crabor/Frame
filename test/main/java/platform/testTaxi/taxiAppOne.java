package platform.testTaxi;

import app.AbstractApp;
import platform.Platform;
import platform.struct.CmdType;
import platform.struct.ServiceType;

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
    public void getMsg(String channel, String msg) {
        logger.debug(appName + " recv " + msg);
        System.out.println(appName + " recv " + msg);
        if(msg.contains("2011-04-08-04:00:00:000")){
            call(ServiceType.CTX, CmdType.RESET);
        }
    }
}
