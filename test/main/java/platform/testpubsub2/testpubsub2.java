package platform.testpubsub2;

import io.lettuce.core.RedisClient;
import platform.pubsub.AbstractSubscriber;
import platform.pubsub.Channel;
import platform.pubsub.Publisher;

public class testpubsub2 {
    public static void main(String[] args) throws InterruptedException {
        RedisClient client = RedisClient.create("redis://localhost:6379");
        Publisher.Init(client);
        AbstractSubscriber.Init(client);

        Channel sensor = new Channel("sensor");
        Channel actor = new Channel("actor");
        Channel check = new Channel("check");

        UI ui = new UI();
        INV inv = new INV();

        ui.subscribe(sensor);
        ui.subscribe(actor);
        ui.subscribe(check);
        inv.subscribe(sensor);
        inv.subscribe(check);

        Thread.sleep(1000);
        System.out.println(Channel.getObjs());

//        Publisher p = new Publisher();
//        p.publish(sensor, "sensor");
        //p.publish(actor, "actor");
        //p.publish(check, "check");

        while(true);
    }
}
