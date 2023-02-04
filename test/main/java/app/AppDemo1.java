package app;

import app.struct.SensorInfo;

import java.util.Map;

public class AppDemo1 extends AbstractApp {
    @Override
    public void setting() {
        //sensor setting
//        Map<String, SensorInfo> supportedSensors = getSupportedSensors();
//        if (supportedSensors.containsKey("front")) {
//            registerSensor("front");
//        }

        //ctx setting
//        setRuleFile("Resources/taxiTest/appOne/taxiRules.xml");
//        setBfuncFile("Resources/taxiTest/appOne/taxiBfunction.class");
//        setPatternFile("Resources/taxiTest/appOne/taxiPatterns.xml");
//        setMfuncFile("Resources/taxiTest/appOne/taxiMfunction.class");
//        call(ServiceType.CTX, CmdType.START);
    }

    @Override
    public void getMsg(String channel, String msg) {
        // 因为注册了front传感器，所以每当front有新的数据时便会触发getMsg，
        // 其中channel是sensor名字，msg是sensor数据
        logger.info(String.format("[%s]: getMsg(channel, msg) -> %s, %s", appName, channel, msg));
        //用户代码
    }

    public static void main(String[] args) {
        try {
            AppDemo1 app = new AppDemo1();
            app.registerApp("127.0.0.1", 8888);
            Map<String, SensorInfo> supportedSensors = app.getSupportedSensors();
            if (supportedSensors.containsKey("front")) {
                app.registerSensor("front");
            }
            Thread.sleep(2000);
            if (supportedSensors.containsKey("back")) {
                app.registerSensor("back");
            }
            Thread.sleep(2000);
            app.getRegisteredSensors();
            Thread.sleep(2000);
            app.cancelSensor("front");
            Thread.sleep(2000);
            app.getRegisteredSensors();
            Thread.sleep(10000);
            app.cancelApp();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
