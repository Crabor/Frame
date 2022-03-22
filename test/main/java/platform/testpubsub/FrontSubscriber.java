package platform.testpubsub;

import platform.pubsub.AbstractSubscriber;

public class FrontSubscriber extends AbstractSubscriber {
    @Override
    public void message(String s, String s2) {
//        System.out.println("I am FrontSubscriber, I get msg from '" + s + "' : " + s2);
        TestPubSub.latch.countDown();
    }

    @Override
    public void message(String s, String k1, String s2) {

    }

    @Override
    public void subscribed(String s, long l) {

    }

    @Override
    public void psubscribed(String s, long l) {

    }

    @Override
    public void unsubscribed(String s, long l) {

    }

    @Override
    public void punsubscribed(String s, long l) {

    }
}
