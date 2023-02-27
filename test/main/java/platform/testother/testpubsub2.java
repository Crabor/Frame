package platform.testother;

import io.lettuce.core.RedisClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import platform.communication.pubsub.AbstractSubscriber;
import platform.communication.pubsub.Channel;
import platform.communication.pubsub.Publisher;
import platform.communication.pubsub.GrpPrioPair;

public class testpubsub2 {
    static Log logger = LogFactory.getLog(testpubsub2.class);

    public static void main(String[] args) throws InterruptedException {
        //init database
//        RedisClient client = RedisClient.create("redis://localhost:6379");
//        Publisher.Init(client);
//        AbstractSubscriber.Init(client);
//
//        Channel c = new Channel("channel");
//        AbstractSubscriber s1 = new AbstractSubscriber() {
//            @Override
//            public void onMessage(String channel, String msg) {
//                System.out.println("s1 recv " + msg);
//            }
//        };
//        s1.subscribe(c, 0, 0);
//        s1.subscribe(c, 1, 0);
//
//        AbstractSubscriber s2 = new AbstractSubscriber() {
//            @Override
//            public void onMessage(String channel, String msg) {
//                System.out.println("s2 recv " + msg);
//                GrpPrioPair gpm = getGrpPrioPair(channel);
//                publish(c, gpm.groupId, gpm.priorityId - 1, msg + msg);
//            }
//        };
//        s2.subscribe(c,1, 1);
//
//        Publisher p = new Publisher();
//        p.publish(c, "hello");
//
//        while (true);

//
//        AbstractSubscriber s2 = new AbstractSubscriber() {
//            @Override
//            public void onMessage(String channel, String msg) {
//                System.out.println("s2:" + msg);
//            }
//        };
//        Subscribe sub2 = s2.subscribe(c, 0, 1, -1);
//
//        AbstractSubscriber s3 = new AbstractSubscriber() {
//            @Override
//            public void onMessage(String channel, String msg) {
//                System.out.println("s3:" + msg);
//            }
//        };
//        s3.subscribe(c, 1, 0);
//
//        logger.info("channels: ");
//        for (Channel channel : Channel.getObjs()) {
//            logger.info(channel);
//        }
//        logger.info("subscribers: " + AbstractSubscriber.getObjs());
//
//        Thread.sleep(2000);
//        for (int i = 0; i < 50; i++) {
//            p.publish(c,String.valueOf(System.currentTimeMillis()));
//        }
//        new Thread(() -> {
//            Publisher p = new Publisher();
//            while (true) {
//
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//                p.publish(c, String.valueOf(System.currentTimeMillis()));
//            }
//        }).start();
//
//        Scanner sc = new Scanner(System.in);
//        while (true) {
//            String str = sc.nextLine();
//            LockSupport.unpark(sub.getThread());
//        }
    }
}
