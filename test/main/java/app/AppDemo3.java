package app;

import common.struct.SensorData;

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

    public static void main(String[] args) {
        AppDemo3 app = new AppDemo3();
        RemoteConnector connector = RemoteConnector.getInstance();
        connector.connectPlatform("127.0.0.1", 8079);
        connector.registerApp(app);
        connector.checkConnected();
        connector.unregisterApp(app);
        connector.disConnectPlatform();
    }
}
