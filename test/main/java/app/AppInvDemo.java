package app;

import app.struct.SensorInfo;
import common.struct.InvServiceConfig;
import common.struct.SensorData;
import common.struct.enumeration.*;
import common.struct.sync.SynchronousSensorData;

import java.util.HashMap;
import java.util.Map;

public class AppInvDemo extends AbstractApp {
    Map<String, SynchronousSensorData> invReport = new HashMap<>();
    @Override
    public void getMsg(String sensorName, SensorData value) {
//        System.out.println("getMsg: " + sensorName + " " + value);
        if (value.getType() == SensorDataType.INV_REPORT) {
            invReport.computeIfAbsent(sensorName, k -> new SynchronousSensorData()).put(value);
        }
    }

    @Override
    public void configApp() {

    }

    public static void main(String[] args) throws InterruptedException {
        AppInvDemo app = new AppInvDemo();
        AppRemoteConnector connector = AppRemoteConnector.getInstance();
        connector.connectPlatform("127.0.0.1", 9090);
        connector.registerApp(app);
        InvServiceConfig config = new InvServiceConfig();
        config.setInitThro(10);
        config.setGenThro(100);
        connector.serviceStart(ServiceType.INV, config);
        InvCheck checker = InvCheck.getInstance();
        Map<String, SensorInfo> supportedSensors = connector.getSupportedSensors();
        if (supportedSensors.containsKey("YellowCar")) {
            connector.registerSensor("YellowCar", SensorMode.ACTIVE, -1);
        }
        connector.getMsgThread(CmdType.START);

        int x = 0, y = 0, z = 0, index = 0;
        checker.monitor(x, y, z);

        while (true) {
            index++;
            x = index;
            y = index % 38;
            z = x * y;
            checker.check(x, y, z);
            Thread.sleep(10);

            SensorData data =
                    app.invReport.computeIfAbsent("INV_REPORT51", k -> new SynchronousSensorData()).blockTake();
            CheckResult result = checker.getResult(data);
            System.out.println(result);
            if (result != CheckResult.INV_GENERATING) {
                break;
            }
        }

        connector.serviceStop(ServiceType.INV);
        connector.getMsgThread(CmdType.STOP);
        connector.unregisterApp(app);
        connector.disConnectPlatform();
    }
}
