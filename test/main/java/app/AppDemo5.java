package app;

import app.struct.SensorInfo;
import common.struct.SensorData;
import common.struct.enumeration.CmdType;
import common.struct.enumeration.SensorMode;

import java.util.Map;

public class AppDemo5 extends AbstractApp {
    @Override
    public void getMsg(String sensorName, SensorData value) {

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
        Map<String, SensorInfo> supportedSensors = connector.getSupportedSensors();
        if (supportedSensors.containsKey(sensorName)) {
            connector.registerSensor(sensorName, SensorMode.PASSIVE, 1);
        }
        connector.getMsgThread(CmdType.START);
        while (true);
//        Thread.sleep(10000);
//        connector.cancelAllSensors();
//        connector.unregisterApp(demo);
//        connector.disConnectPlatform();
    }
}
