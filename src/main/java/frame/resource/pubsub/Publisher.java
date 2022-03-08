package frame.resource.pubsub;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class Publisher {
    static JedisPool jedisPool;
    String channel;

    public static void Init(JedisPool jedisPool) {
        Publisher.jedisPool = jedisPool;
    }

    public Publisher(String channel) {
        this.channel = channel;
    }

    public void publish(String message) {
        Jedis jedis = jedisPool.getResource();
        jedis.publish(channel, message);
        jedis.close();
    }
}
