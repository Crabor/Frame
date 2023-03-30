package app;

import app.struct.SensorInfo;
import common.struct.SensorData;
import common.struct.State;
import common.struct.enumeration.CmdType;
import common.struct.enumeration.SensorMode;

import java.util.Map;

public class AppDemo9 extends AbstractApp {
    @Override
    public void getMsg(String sensorName, SensorData value) {
        logger.info(String.format("[%s]: getMsg(channel, msg) -> %s, %s", appName, sensorName, value));
    }

    @Override
    public void configApp() {

    }

    public static void main(String[] args) throws InterruptedException {
        AppDemo9 demo = new AppDemo9();
        AppRemoteConnector connector = AppRemoteConnector.getInstance();
        connector.connectPlatform("127.0.0.1", 8079);
        connector.registerApp(demo);

        Map<String, SensorInfo> supportedSensors = connector.getSupportedSensors();
        supportedSensors.forEach((sensorName, sensorInfo) -> {
            if (sensorInfo.state == State.ON) {
                connector.registerSensor(sensorName, SensorMode.PASSIVE, 10);
            }
        });
        connector.getMsgThread(CmdType.START);
        while (true);
//        Thread.sleep(10000);
//        connector.cancelAllSensors();
//        connector.unregisterApp(demo);
//        connector.disConnectPlatform();
    }
}
