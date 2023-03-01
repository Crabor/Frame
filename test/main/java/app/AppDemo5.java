package app;

import app.struct.ActorInfo;
import app.struct.SensorInfo;
import common.struct.SensorData;
import common.struct.State;
import common.struct.enumeration.CmdType;
import common.struct.enumeration.SensorMode;

import java.util.Map;

public class AppDemo5 extends AbstractApp {
    @Override
    public void getMsg(String sensorName, SensorData value) {
//        logger.info(String.format("[%s]: getMsg(channel, msg) -> %s, %s", appName, sensorName, value));
    }

    @Override
    public void configApp() {

    }

    public static void main(String[] args) throws InterruptedException {
        AppDemo5 demo = new AppDemo5();
        RemoteConnector connector = RemoteConnector.getInstance();
        connector.connectPlatform("127.0.0.1", 8079);
        connector.registerApp(demo);
        String sensorName = "GPS_001";
        String actorName = "xSpeed";
        Map<String, ActorInfo> supportedActors = connector.getSupportedActors();
        if (supportedActors.containsKey(actorName) && supportedActors.get(actorName).state == State.ON) {
            connector.registerActor(actorName);
        }
        connector.setActorCmd(actorName, "10");
        while (true);
//        Thread.sleep(10000);
//        connector.cancelAllSensors();
//        connector.unregisterApp(demo);
//        connector.disConnectPlatform();
    }
}
