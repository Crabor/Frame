package platform.testcarapp;

import platform.Platform;
import platform.app.userapps.MySyncApp;
import platform.pubsub.AbstractSubscriber;
import platform.pubsub.Channel;

public class demo {
    public static void main(String[] args) throws InterruptedException {
        Platform.Init();
        Platform.Start();
    }
}
