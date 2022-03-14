package frame.service;

import com.alibaba.fastjson.JSONObject;
import frame.resource.pubsub.Subscriber;
import redis.clients.jedis.Jedis;

public class SerSubThread extends Subscriber implements Runnable {
    SerMgrThread service;
    Thread t;

    public SerSubThread(SerMgrThread service) {
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

    public void start() {
        if (t == null) {
            t = new Thread (this, "SerSubThread");
            t.start ();
        }
    }
}
