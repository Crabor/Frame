package platform.testunitycar;


import app.AbstractApp;
import common.struct.SensorData;

public class MyApp extends AbstractApp {

    @Override
    public void configApp() {
//        config.registerSensor("left");
//        config.registerSensor("right");
//        config.registerSensor("front");
//        config.registerSensor("back");
    }

    @Override
    public void getMsg(String sensorName, SensorData value) {
        logger.debug("app recv: " + value);

        //method 1
//        CheckObject left = CheckObject.fromJsonObjectString(msg);
//        CheckInfo checkInfo = left.check();
//        logger.debug("check:\n" + JSON.toJSONString(checkInfo, true));
//        if (checkInfo.checkState == CheckState.INV_VIOLATED) {
//            Cmd ySpeed = new Cmd("actor_set", "ySpeed", String.valueOf(-checkInfo.diff));
//            logger.debug(ySpeed);
//            PlatformUDP.send(ySpeed);
//        }

//        Cmd xSpeed = new Cmd("actor_set", "xSpeed", "5");
//        PlatformUDP.send(xSpeed);
//        logger.debug(xSpeed);
    }
}
