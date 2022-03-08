package frame.service;

import com.alibaba.fastjson.JSONObject;
import frame.resource.pubsub.Subscriber;
import frame.service.Service;
import redis.clients.jedis.Jedis;

public class ServiceSubThread extends Subscriber implements Runnable {
    Service service;

    public ServiceSubThread(Service service) {
        this.service = service;
    }

    @Override
    protected void onSensorChannel(JSONObject message) {
        // TODO
    }

    @Override
    protected void onActorChannel(JSONObject message) {
        // TODO
    }

    @Override
    protected void onDumpChannel(JSONObject message) {

    }

    @Override
    public void run() {
        Jedis jedis = jedisPool.getResource();
        jedis.subscribe(this, "SENSOR", "ACTOR");
        jedis.close();
    }
}
