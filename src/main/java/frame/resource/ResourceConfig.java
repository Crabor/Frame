package frame.resource;

import frame.struct.sensor.Type;
import redis.clients.jedis.JedisPool;

import java.util.Map;

public class ResourceConfig {
    public String redisServerPath;
    public String redisServerConfPath;

    // TODO:实现从文本中读取resource配置
    public static ResourceConfig readConfig(String filename) {
        return null;
    }
}
