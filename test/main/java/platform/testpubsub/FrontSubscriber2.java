package platform.testpubsub;

import platform.pubsub.AbstractSubscriber;

public class FrontSubscriber2 extends AbstractSubscriber {
    @Override
    public void message(String s, String s2) {
        System.out.println("I am FrontSubscriber2, I get msg from '" + s + "' : " + s2);
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
