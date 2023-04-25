package app;

import app.struct.ActorInfo;
import app.struct.SensorInfo;
import common.struct.SensorData;
import common.struct.State;
import common.struct.enumeration.CmdType;
import common.struct.enumeration.SensorMode;

import java.util.Map;

public class AppDemo3 extends AbstractApp {
    @Override
    public void getMsg(String sensorName, SensorData value) {
        logger.info(String.format("[%s]: getMsg(channel, msg) -> %s, %s", appName, sensorName, value));
    }

    @Override
    public void configApp() {
        this.appName = "Demo3";
        this.appDescription = "This is Demo3";
    }

    public static void main(String[] args) throws InterruptedException {
        AppDemo3 app = new AppDemo3();
        AppRemoteConnector connector = AppRemoteConnector.getInstance();
        connector.connectPlatform("127.0.0.1", 9090);
        connector.registerApp(app);
        connector.checkConnected();

        Map<String, SensorInfo> supportedSensors = connector.getSupportedSensors();
        if (supportedSensors.containsKey("unitycar") && supportedSensors.get("unitycar").state == State.ON) {
            connector.registerSensor("unitycar", SensorMode.ACTIVE, -1);
            connector.getSensorData("unitycar");
        }

        Map<String, ActorInfo> supportedActors = connector.getSupportedActors();
        if (supportedActors.containsKey("unitycar") && supportedActors.get("unitycar").state == State.ON) {
            connector.registerActor("unitycar");
            connector.setActorCmd("unitycar", "xSpeed 5");
        }
        connector.unregisterApp(app);
        connector.disConnectPlatform();
    }
}
