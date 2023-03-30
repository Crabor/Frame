package app;

import common.struct.CtxServiceConfig;
import common.struct.SensorData;
import common.struct.enumeration.CmdType;
import common.struct.enumeration.SensorMode;
import common.struct.enumeration.ServiceType;

public class Test extends AbstractApp {
    public static Test app;
    public static AppRemoteConnector connector = AppRemoteConnector.getInstance();
    public static CtxServiceConfig ctxServiceConfig = new CtxServiceConfig();

    @Override
    public void configApp() {
        this.appName = "app.Test";
        this.appDescription = " ";
    }

    @Override
    public void getMsg(String sensorName, SensorData value) {
        System.out.println(value.getAllData());
    }

    public static void main(String[] args) throws InterruptedException {
        app = new Test();
        connector.connectPlatform("127.0.0.1", 8888);
        connector.registerApp(app);

        connector.registerSensor("context", SensorMode.PASSIVE, 10);
        connector.registerActor("Yellow_Car");
        connector.registerActor("Blue_Car");

        ctxServiceConfig.setCtxResources("Resources/simcityTest/rules_blue.xml",
                "Resources/simcityTest/patterns_blue.xml",
                "Resources/simcityTest/bfuncs.java",
                null,
                null);
        connector.serviceStart(ServiceType.CTX, ctxServiceConfig);

        connector.getMsgThread(CmdType.START);

        Thread.sleep(15000);

        connector.serviceStop(ServiceType.CTX);
        connector.cancelAllSensors();
        connector.cancelAllActors();
        connector.unregisterApp(app);
        connector.disConnectPlatform();
    }

}
