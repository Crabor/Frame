package frame.app;

import com.alibaba.fastjson.JSONObject;
import frame.pubsub.Subscriber;
import redis.clients.jedis.Jedis;

public class AppSubThread extends Subscriber implements Runnable {
    AppMgrThread appMgr;
    Thread t;

    public AppSubThread(AppMgrThread appMgr) {
        this.appMgr = appMgr;
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

    public void start() {
        if (t == null) {
            t = new Thread (this, "AppSubThread");
            t.start ();
        }
    }
}
