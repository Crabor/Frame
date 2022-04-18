package platform.struct;

import platform.pubsub.AbstractSubscriber;

public class SubscriberCutPair {
    public AbstractSubscriber subscriber;
    public boolean cut;

    public SubscriberCutPair(AbstractSubscriber subscriber, boolean cut) {
        this.subscriber = subscriber;
        this.cut = cut;
    }

    @Override
    public String toString() {
        return "{" + subscriber + "," + cut + "}";
    }
}
