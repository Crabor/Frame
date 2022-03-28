package platform.testcarapp;

import platform.Platform;
import platform.pubsub.Channel;
import platform.resource.device.DeviceRTThread;
import platform.resource.device.DeviceSubscriber;

import java.awt.*;

public class testcarapp {
    public static void main(String[] args) {
        Platform.Init();
        Channel sensor = new Channel("sensor");
        Channel actor = new Channel("actor");
        DeviceSubscriber ds = new DeviceSubscriber();
        ds.subscribe(actor);
        DeviceRTThread dt = DeviceRTThread.getInstance();
        ds.bind(dt);
        MyAppSubscriber my = new MyAppSubscriber();
        my.subscribe(sensor);

        dt.start();
    }
}
