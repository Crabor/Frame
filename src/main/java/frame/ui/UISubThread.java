package frame.ui;

import com.alibaba.fastjson.JSONObject;
import frame.resource.pubsub.Subscriber;
import frame.ui.UI;
import redis.clients.jedis.Jedis;

public class UISubThread extends Subscriber implements Runnable {
    UI ui;

    public UISubThread(UI ui) {
        this.ui = ui;
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
        // TODO
    }

    @Override
    public void run() {
        Jedis jedis = jedisPool.getResource();
        jedis.subscribe(this, "SENSOR", "ACTOR", "DUMP");
        jedis.close();
    }
}
