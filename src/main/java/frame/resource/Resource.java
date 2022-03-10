package frame.resource;

import frame.resource.pubsub.Publisher;
import frame.resource.pubsub.Subscriber;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Resource {
    JedisPool jedisPool;

    public Resource(ResourceConfig config) throws IOException {
        //startup redis server
//        String[] cmd = new String[]{"cmd", "/c", config.redisServerPath, config.redisServerConfPath};
//        Runtime.getRuntime().exec(cmd);

        //startup redis client pool
        String host = "127.0.0.1";
        int port = 6379;
//        List<String> lines = Files.readAllLines(Paths.get(config.redisServerConfPath), StandardCharsets.UTF_8);
//        for (String line : lines) {
//            if (line.length() == 0 || line.charAt(0) == '#') {
//                continue;
//            }
//            String[] conf = line.split(" ");
//            if (conf[0].equals("bind")) {
//                host = conf[1];
//            } else if (conf[0].equals("port")) {
//                port = Integer.parseInt(conf[1]);
//                break;
//            }
//        }
        jedisPool = new JedisPool(host, port);

        //init pubsub
        Publisher.Init(jedisPool);
        Subscriber.Init(jedisPool);
    }
}
