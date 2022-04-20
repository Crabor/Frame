package platform.testpubsub2;

import platform.pubsub.AbstractSubscriber;

public class INV extends AbstractSubscriber {
    @Override
    public void onMessage(String channel, String msg) {
    }

    @Override
    public void onSubscribed(String channel, long subChannelCount) {

    }

    @Override
    public void onUnsubscribed(String channel, long subChannelCount) {

    }
}
