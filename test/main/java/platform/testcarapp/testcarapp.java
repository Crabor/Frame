package platform.testcarapp;

import platform.Platform;
import platform.pubsub.Channel;
import platform.resource.device.DeviceDriver;

public class testcarapp {
    public static void main(String[] args) {
        Platform.Init();
        Channel sensor = new Channel("sensor");
        Channel actor = new Channel("actor");
        DeviceDriver dd = new DeviceDriver(8080, "127.0.0.1", 8081);
        dd.subscribe(actor);
        dd.start();
        MyAppSubscriber my = new MyAppSubscriber();
        my.subscribe(sensor);
        while(true);
    }
}
