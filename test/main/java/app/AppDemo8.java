package app;

import app.struct.ActorInfo;
import app.struct.SensorInfo;
import common.struct.SensorData;
import common.struct.State;
import common.struct.enumeration.CmdType;
import common.struct.enumeration.SensorMode;

import java.util.Map;

public class AppDemo8 extends AbstractApp {
    @Override
    public void getMsg(String sensorName, SensorData value) {

    }

    @Override
    public void configApp() {

    }

    public static void main(String[] args) {
        AppDemo8 demo = new AppDemo8();
        RemoteConnector connector = RemoteConnector.getInstance();
        connector.connectPlatform("127.0.0.1", 8079);
        connector.registerApp(demo);

        String actorName = "xSpeed";
        Map<String, ActorInfo> supportedActors = connector.getSupportedActors();
        if (supportedActors.containsKey(actorName)
        && supportedActors.get(actorName).state == State.ON) {
            connector.registerActor(actorName);
        }
        connector.setActorCmd(actorName, "100");

        connector.cancelAllActors();
        connector.unregisterApp(demo);
        connector.disConnectPlatform();
    }
}
