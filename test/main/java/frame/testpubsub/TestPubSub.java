package frame.testpubsub;

import com.alibaba.fastjson.JSONObject;
import frame.pubsub.AbstractSubscriber;
import frame.pubsub.Channel;
import frame.pubsub.Publisher;
import frame.struct.GrpPrioPair;
import io.lettuce.core.RedisClient;

import java.util.concurrent.CountDownLatch;

public class TestPubSub {
    private static final int M = 1;
    private static final int N = 1000;
    public static final CountDownLatch latch = new CountDownLatch(M * N);
    public static void main(String[] args) throws InterruptedException {
        //init
        RedisClient client = RedisClient.create("redis://localhost:6379");
        Publisher.Init(client);
        AbstractSubscriber.Init(client);

        Channel sensor = new Channel("sensor");
        SensorSubscriber ss = new SensorSubscriber();

        ss.subscribe(sensor);
        for (int i = 0; i < M; i++) {
            Channel tmp = new Channel(String.valueOf(i));
            CommSubscriber cs = new CommSubscriber();
            cs.subscribe(tmp);
        }

        //publisher
        Publisher publisher = new Publisher();

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < N; i++) {
            JSONObject jo = new JSONObject();
            for (int j = 0; j < M; j++) {
                jo.put(String.valueOf(j), "hello");
            }
            publisher.publish(sensor, jo.toString());
        }
        latch.await();//等待所有子线程结束
        long endTime = System.currentTimeMillis();
        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");    //输出程序运行时间
    }
}
