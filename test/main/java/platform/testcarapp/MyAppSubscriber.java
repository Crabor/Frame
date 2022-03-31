package platform.testcarapp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import platform.pubsub.AbstractSubscriber;
import platform.struct.Actor;

public class MyAppSubscriber extends AbstractSubscriber {
    @Override
    public void onMessage(String s, String s2) {
        if (s.equals("sensor")) {
            JSONObject jo = JSON.parseObject(s2);
            Double front = jo.getDouble("front");
            if (front > 10) {
                publish("actor", new Actor(0, 10).toString());
            } else {
                publish("actor", new Actor(0, 0).toString());
            }
        }
    }

    @Override
    public void onSubscribed(String s, long l) {

    }

    @Override
    public void onUnsubscribed(String s, long l) {

    }
}
