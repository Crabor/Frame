package frame.resource.pubsub;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

public abstract class Subscriber extends JedisPubSub {
    protected static JedisPool jedisPool;

    public static void Init(JedisPool jedisPool) {
        Subscriber.jedisPool = jedisPool;
    }

    public void onMessage(String channel, String message) {
        JSONObject json = JSON.parseObject(message);
        if (channel.equalsIgnoreCase("SENSOR")) {
            onSensorChannel(json);
        } else if (channel.equalsIgnoreCase("ACTOR")) {
            onActorChannel(json);
        } else if (channel.equalsIgnoreCase("DUMP")) {
            onDumpChannel(json);
        } else {
            System.err.println("Unknown channel : " + channel);
        }
    }

    protected abstract void onSensorChannel(JSONObject message);

    protected abstract void onActorChannel(JSONObject message);

    protected abstract void onDumpChannel(JSONObject message);
//
//    public void onSubscribe(String channel, int subscribedChannels) {
//    }
//
//    public void onUnsubscribe(String channel, int subscribedChannels) {
//    }
//
//    public void onPSubscribe(String pattern, int subscribedChannels) {
//    }
//
//    public void onPUnsubscribe(String pattern, int subscribedChannels) {
//    }
//
//    public void onPMessage(String pattern, String channel, String message) {
//    }
}
