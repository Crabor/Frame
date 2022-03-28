package platform.resource.device;

import platform.pubsub.AbstractSubscriber;

public class DeviceSubscriber extends AbstractSubscriber {
    @Override
    public void onMessage(String s, String s2) {
        if (thread != null) {
            DeviceRTThread dt = (DeviceRTThread) thread;
            dt.transmit(s2);
        }
    }

    @Override
    public void onSubscribed(String s, long l) {

    }

    @Override
    public void onUnsubscribed(String s, long l) {

    }
}
