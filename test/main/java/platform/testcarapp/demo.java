package platform.testcarapp;

import platform.Platform;
import platform.app.userapps.MySyncApp;
import platform.pubsub.Channel;

public class demo {
    public static void main(String[] args) throws InterruptedException {
        Platform.Init();
        Platform.Start();

        Thread.sleep(5000);
        for (Channel c : Channel.getObjs()) {
            System.out.println(c);
        }

        while(true);
    }
}
