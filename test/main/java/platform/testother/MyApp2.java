package platform.testother;

import platform.app.AbstractApp;
import platform.config.Configuration;
import platform.resource.ResMgrThread;
import platform.util.Util;

public class MyApp2 extends AbstractApp {
    @Override
    protected void customizeCtxServer() {
//        config.registerSensor(sensor);
//        ......
    }

    @Override
    public void iter(String channel, String msg) {
//        if (sensor) {
//            //sensor to ctx
//
//            //register "sensor"
//            if (!Configuration.isSensorExists(ctx)) {
//                Configuration.addSensorConfig(ctx, type, fieldName);
//                config.registerSensor(ctx);
//            }
//
//            //transmit "sensor"
//            publish("sensor", 0, ctxJsonString);
//        } else if (ctx) {
//
//        }
    }
}
