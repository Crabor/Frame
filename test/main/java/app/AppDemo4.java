package app;

import app.struct.SensorInfo;
import common.struct.State;
import common.struct.SensorData;
import common.struct.sync.SynchronousSensorData;
import common.struct.enumeration.CmdType;
import common.struct.enumeration.SensorMode;

import java.util.HashMap;
import java.util.Map;

public class AppDemo4 extends AbstractApp {
    Map<String, SynchronousSensorData> dataCurrent = new HashMap<>();

    @Override
    public void getMsg(String sensorName, SensorData value) {
//        logger.info(String.format("[%s]: getMsg(channel, msg) -> %s, %s", appName, sensorName, value));
        dataCurrent.computeIfAbsent(sensorName, k -> new SynchronousSensorData()).put(value);
    }

    @Override
    public void configApp() {
        this.appName = "Demo4";
        this.appDescription = "This is Demo4";
    }

    public static void main(String[] args) {
        AppDemo4 demo = new AppDemo4();
        AppRemoteConnector connector = AppRemoteConnector.getInstance();
        connector.connectPlatform("127.0.0.1", 8079);
        connector.registerApp(demo);
        String sensorName = "GPS_001";
        Map<String, SensorInfo> supportedSensors = connector.getSupportedSensors();
        if (supportedSensors.containsKey(sensorName)
        && supportedSensors.get(sensorName).state == State.ON) {
            connector.registerSensor(sensorName, SensorMode.PASSIVE, 10);
        }
        connector.getMsgThread(CmdType.START);
        SensorData data1 = demo.dataCurrent.computeIfAbsent(sensorName, k -> new SynchronousSensorData()).blockTake();
        System.out.printf("Sensor data for %s is %s%n", sensorName, data1);

        SensorData data2;
        do {
            data2 = demo.dataCurrent.computeIfAbsent(sensorName, k -> new SynchronousSensorData()).nonBlockTake();
        } while (data2 == null);
        System.out.printf("Sensor data for %s is %s%n", sensorName, data2);

        connector.cancelAllSensors();
        connector.unregisterApp(demo);
        connector.disConnectPlatform();
    }
}
