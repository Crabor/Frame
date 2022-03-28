package platform.testpubsub;

import com.alibaba.fastjson.JSONObject;
import platform.pubsub.AbstractSubscriber;

public class SensorSubscriber extends AbstractSubscriber {
    @Override
    public void onMessage(String s, String s2) {
        JSONObject jo = JSONObject.parseObject(s2);
        jo.forEach((key, value) -> {
            JSONObject j = new JSONObject();
            j.put(key, value.toString());
            publisher.publish(key, j.toString());
        });
    }

    @Override
    public void onSubscribed(String s, long l) {

    }

    @Override
    public void onUnsubscribed(String s, long l) {

    }
}
