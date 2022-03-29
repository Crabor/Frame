package platform.resource.device;

import platform.pubsub.AbstractSubscriber;

public class DeviceDriver extends AbstractSubscriber {
    private int receivePort = 8080;
    private int sendPort = 8081;

    @Override
    public void onMessage(String s, String s2) {

    }

    @Override
    public void onSubscribed(String s, long l) {

    }

    @Override
    public void onUnsubscribed(String s, long l) {

    }
}
