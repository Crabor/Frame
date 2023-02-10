package app;

import app.struct.ActuatorInfo;
import app.struct.SensorInfo;
import common.struct.CmdType;
import common.struct.SensorModeType;

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
    public void getMsg(String sensorName, String value) {
        // 因为注册了front传感器，所以每当front有新的数据时便会触发getMsg，
        // 其中channel是sensor名字，msg是sensor数据
        logger.info(String.format("[%s]: getMsg(sensorName, value) -> %s, %s", appName, sensorName, value));
        //用户代码
    }

    public static void main(String[] args) {
        try {
            AppDemo1 app = new AppDemo1();
            app.connect("127.0.0.1", 8888);
//            app.setRuleFile("Resources/taxiTest/appOne/taxiRules.xml");
//            app.setBfuncFile("Resources/taxiTest/appOne/taxiBfunction.class");
//            app.setPatternFile("Resources/taxiTest/appOne/taxiPatterns.xml");
//            app.setMfuncFile("Resources/taxiTest/appOne/taxiMfunction.class");
//            Map<String, ActuatorInfo> supportedActuators = app.getSupportedActuators();
//            if (supportedActuators.containsKey("xSpeed")) {
//                app.registerActuator("xSpeed");
//            }
//            app.getRegisteredActuators();
//            if (supportedActuators.containsKey("ySpeed")) {
//                app.registerActuator("ySpeed");
//            }
//            app.getRegisteredActuators();
//            app.getRegisteredActuatorStatus();
//            app.setActuator("xSpeed", "5");
//            app.cancelActuator("xSpeed");
//            app.setActuator("xSpeed", "6");
//            app.getRegisteredActuators();
//            app.cancelAllActuators();
//            app.getRegisteredActuators();
            Map<String, SensorInfo> supportedSensors = app.getSupportedSensors();
            if (supportedSensors.containsKey("front")) {
                app.registerSensor("front", SensorModeType.PASSIVE);
            }
            app.getMsgThread(CmdType.START);
//            Thread.sleep(2000);
//            app.registerSensor("back", SensorModeType.PASSIVE);
//            Thread.sleep(2000);
//            app.getRegisteredSensors();
//            app.getSensorData("front");
//            app.getAllSensorData();
//            app.cancelAllSensors();
//            app.getRegisteredSensors();
//            app.registerSensor("front", SensorModeType.PASSIVE);
//            app.getMsgThread(CmdType.START);
//            Thread.sleep(2000);
//            app.getRegisteredSensorsStatus();
//            Thread.sleep(2000);
//            app.cancelSensor("front");
//            Thread.sleep(2000);
//            if (supportedSensors.containsKey("back")) {
//                app.registerSensor("back", SensorModeType.PASSIVE);
//            }
            Thread.sleep(2000);
            app.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
