package frame.app;

import com.alibaba.fastjson.JSONObject;
import frame.app.App;
import frame.resource.pubsub.Subscriber;
import redis.clients.jedis.Jedis;

public class AppSubThread extends Subscriber implements Runnable {
    App app;

    public AppSubThread(App app) {
        this.app = app;
    }

    @Override
    protected void onSensorChannel(JSONObject message) {
        // TODO
    }

    @Override
    protected void onActorChannel(JSONObject message) {

    }

    @Override
    protected void onDumpChannel(JSONObject message) {

    }

    @Override
    public void run() {
        Jedis jedis = jedisPool.getResource();
        jedis.subscribe(this, "SENSOR");
        jedis.close();
    }
}
