package platform.testpubsub;

import com.alibaba.fastjson.JSONObject;
import platform.pubsub.AbstractSubscriber;

public class SensorSubscriber extends AbstractSubscriber {
    @Override
    public void message(String s, String s2) {
        JSONObject jo = JSONObject.parseObject(s2);
        jo.forEach((key, value) -> {
            JSONObject j = new JSONObject();
            j.put(key, value.toString());
            publisher.publish(key, j.toString());
        });
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
