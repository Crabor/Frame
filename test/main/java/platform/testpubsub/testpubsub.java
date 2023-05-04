package platform.testpubsub;

import platform.communication.pubsub.AbstractSubscriber;
import platform.communication.pubsub.Channel;
import platform.communication.pubsub.GrpPrioPair;
import platform.communication.pubsub.Publisher;

public class testpubsub {
    public static void main(String[] args) {
        //test pubsub
        Channel c1 = Channel.get("c1");
        Channel c2 = Channel.get("c2");
        AbstractSubscriber s1 = new AbstractSubscriber() {
            @Override
            public void onMessage(String channel, String msg) {
                System.out.println("s1: " + channel + " " + msg);
            }
        };
        AbstractSubscriber s2 = new AbstractSubscriber() {
            @Override
            public void onMessage(String channel, String msg) {
                System.out.println("s2: " + channel + " " + msg);
            }
        };
        AbstractSubscriber s3 = new AbstractSubscriber() {
            @Override
            public void onMessage(String channel, String msg) {
                System.out.println("s3: " + channel + " " + msg);
                GrpPrioPair pair = getGrpPrioPair(channel);
                System.out.println("s3: " + pair.groupId + " " + pair.priorityId);
                Publisher.publish(channel, pair.groupId, pair.priorityId - 1, "hello");
            }
        };
        s1.subscribe(c1, 1, 1);
        s2.subscribe(c1, 2, 2);
        s3.subscribe(c1, 2, 3);
        s1.unsubscribe(c1);
        Publisher.publish(c1, 1, "hello");
    }
}
