package app;

import app.struct.ActorInfo;
import app.struct.SensorInfo;
import common.struct.SensorData;
import common.struct.State;
import common.struct.enumeration.SensorMode;

import java.util.Map;

public class AppDemo10 extends AbstractApp {
    @Override
    public void getMsg(String sensorName, SensorData value) {
        logger.info(String.format("[%s]: getMsg(channel, msg) -> %s, %s", appName, sensorName, value));
    }

    @Override
    public void configApp() {
        this.appName = "Demo10";
        this.appDescription = "This is Demo10";
    }

    public static void main(String[] args) throws InterruptedException {
        AppDemo10 app = new AppDemo10();
        AppRemoteConnector connector = AppRemoteConnector.getInstance();
        connector.connectPlatform("127.0.0.1", 9090);
        connector.registerApp(app);
        connector.checkConnected();

        Map<String, ActorInfo> supportedActors = connector.getSupportedActors();
        if (supportedActors.containsKey("GreenCar") && supportedActors.get("GreenCar").state == State.ON) {
            connector.registerActor("GreenCar");
            while (true) {
                Thread.sleep(10);
                connector.getRegisteredActors();
                connector.setActorCmd("GreenCar", "forward");
            }
        }
        connector.unregisterApp(app);
        connector.disConnectPlatform();
    }
}
