package frame.ui;

import com.alibaba.fastjson.JSONObject;
import frame.pubsub.Subscriber;
import redis.clients.jedis.Jedis;

public class UISubThread extends Subscriber implements Runnable {
    UIMgrThread uiMgr;
    Thread t;

    public UISubThread(UIMgrThread uiMgr) {
        this.uiMgr = uiMgr;
    }

    @Override
    public void onSensorChannel(JSONObject message) {
        // TODO
    }

    @Override
    public void onActorChannel(JSONObject message) {
        // TODO
    }

    @Override
    public void onDumpChannel(JSONObject message) {
        // TODO
    }

    @Override
    public void run() {
        Jedis jedis = jedisPool.getResource();
        jedis.subscribe(this, "SENSOR", "ACTOR", "DUMP");
        jedis.close();
    }

    public void start() {
        if (t == null) {
            t = new Thread (this, "UISubThread");
            t.start ();
        }
    }
}
