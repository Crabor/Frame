package platform.testpubsub;

import platform.pubsub.AbstractSubscriber;

public class BackSubscriber extends AbstractSubscriber {

    @Override
    public void onMessage(String s, String s2) {
        TestPubSub.latch.countDown();
    }

    @Override
    public void onSubscribed(String s, long l) {

    }

    @Override
    public void onUnsubscribed(String s, long l) {

    }
}
