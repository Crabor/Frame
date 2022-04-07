package platform.app.userapps;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import platform.app.AbstractSyncApp;
import platform.struct.Actor;

public class MySyncApp extends AbstractSyncApp {

    @Override
    public void iter(String channel, String msg) {
        System.out.println("myapp recv: " + msg);
        if (channel.equals("sensor")) {
            JSONObject jo = JSON.parseObject(msg);
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
