package app;

import app.struct.SensorInfo;
import common.struct.CtxServiceConfig;
import common.struct.SensorData;
import common.struct.State;
import common.struct.enumeration.CmdType;
import common.struct.enumeration.SensorMode;
import common.struct.enumeration.ServiceType;

import java.util.Map;

public class zlyAppDemo extends AbstractApp{
    @Override
    public void getMsg(String sensorName, SensorData value) {
        logger.info(String.format("[%s]: getMsg(channel, msg) -> %s, %s", appName, sensorName, value));
    }

    @Override
    public void configApp() {

    }

    public static void main(String[] args) {
        zlyAppDemo demo = new zlyAppDemo();
        AppRemoteConnector connector = AppRemoteConnector.getInstance();
        connector.connectPlatform("127.0.0.1", 8079);
        connector.registerApp(demo);

        String sensorName = "Car";

        Map<String, SensorInfo> supportedSensors = connector.getSupportedSensors();
        if(supportedSensors.containsKey(sensorName) &&
        supportedSensors.get(sensorName).state == State.ON){
            connector.registerSensor(sensorName, SensorMode.PASSIVE, 1);
        }

        CtxServiceConfig ctxServiceConfig = new CtxServiceConfig();
        ctxServiceConfig.setCtxResources("Resources/zlyTest/appOne/rules_yellow.xml",
                "Resources/zlyTest/appOne/patterns_yellow.xml",
                "Resources/zlyTest/appOne/bfuncs.java",
                "Resources/zlyTest/appOne/mfuncs.java",
                null);
        connector.serviceStart(ServiceType.CTX, ctxServiceConfig);
        connector.getMsgThread(CmdType.START);
    }
}
