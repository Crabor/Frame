package frame.testpubsub;

import com.alibaba.fastjson.JSONObject;
import frame.pubsub.AbstractSubscriber;
import frame.pubsub.Channel;
import frame.pubsub.Publisher;
import frame.struct.GrpPrioPair;
import io.lettuce.core.RedisClient;

public class TestPubSub {
    public static void main(String[] args) throws InterruptedException {
        //init
        RedisClient client = RedisClient.create("redis://localhost:6379");
        Publisher.Init(client);
        AbstractSubscriber.Init(client);
        // channels
        Channel sensor = new Channel("sensor");
        Channel front = new Channel("front");
        Channel back = new Channel("back");
        //sensor subscriber
        SensorSubscriber ss = new SensorSubscriber();
        ss.subscribe(sensor);
        //front subscriber
        FrontSubscriber2 fs2 = new FrontSubscriber2();
        fs2.subscribe(front);
        //front2 subscriber
        FrontSubscriber fs = new FrontSubscriber();
        GrpPrioPair p = front.getGrpPrio(fs2);
        fs.subscribe(front, p.groupId, p.priorityId + 1);
        //back subscriber
        BackSubscriber bs = new BackSubscriber();
        bs.subscribe(back);
        //publisher
        Publisher pr = new Publisher();
        int max = 10000;

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < max; i++) {
//            Thread.sleep(3000);
            JSONObject jo = new JSONObject();
            jo.put("front", i);
            jo.put("back", max - i);
            pr.publish(sensor, jo.toString());
        }
        long endTime = System.currentTimeMillis();
        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");    //输出程序运行时间
        while (true);
    }
}
