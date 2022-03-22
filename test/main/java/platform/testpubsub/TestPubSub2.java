package platform.testpubsub;

import platform.pubsub.AbstractSubscriber;
import platform.pubsub.Channel;
import platform.pubsub.Publisher;
import io.lettuce.core.RedisClient;

public class TestPubSub2 {
    public static void main(String[] args) {
        //init
        RedisClient client = RedisClient.create("redis://localhost:6379");
        Publisher.Init(client);
        AbstractSubscriber.Init(client);
        // channels
        Channel sensor = new Channel("sensor");

        SensorSubscriber ss = new SensorSubscriber();
        ss.subscribe(sensor);

        Publisher p = new Publisher();
        p.publish(sensor, "shabi");

        while (true);
    }
}
