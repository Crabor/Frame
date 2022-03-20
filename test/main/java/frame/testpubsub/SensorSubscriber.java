package frame.testpubsub;

import com.alibaba.fastjson.JSONObject;
import frame.pubsub.AbstractSubscriber;
import frame.pubsub.Publisher;

public class SensorSubscriber extends AbstractSubscriber {
    @Override
    public void message(String s, String s2) {
        System.out.println("I am SensorSubscriber, I get msg from '" + s + "' : " + s2);
        System.out.println("I am SensorSubscriber, I am going to decompose and forward the message to channel 'front, back'");

        JSONObject jo = JSONObject.parseObject(s2);
        JSONObject front = new JSONObject();
        front.put("front", jo.getString("front"));
        JSONObject back = new JSONObject();
        back.put("back", jo.getString("back"));

//        Publisher p = new Publisher();
//        p.publish("front", front.toString());
//        p.publish("back", back.toString());
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
