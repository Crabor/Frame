package platform.comm.pubsub;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Publisher {
    private static RedisClient client;
    private static final List<Publisher> objs = new ArrayList<>();
    private static final Lock objsLock = new ReentrantLock();
    private final StatefulRedisConnection<String, String> conn;
    private final Log logger = LogFactory.getLog(Publisher.class);

    public static void Init(RedisClient client) {
        Publisher.client = client;
    }

    public Publisher() {
        conn = client.connect();
        objsLock.lock();
        objs.add(this);
        objsLock.unlock();
    }

    public static void Close() {
        objs.forEach(Publisher::close);
    }

    public void close() {
        conn.close();
    }

    public void publish(Channel channel, int groupId, int priorityId, String message) {
        RedisCommands<String, String> commands = conn.sync();
        Map<Integer, Subscribe> grp = channel.getSubscribes(groupId);
        if (grp != null) {
            int maxPrio = Integer.MIN_VALUE;
            for (Integer prio : grp.keySet()) {
                if (prio > priorityId) {
                    continue;
                }
                maxPrio = Math.max(maxPrio, prio);
            }
            if (maxPrio != Integer.MIN_VALUE) {
                commands.lpush(
                        String.join(
                                "-",
                                channel.getName(),
                                String.valueOf(groupId),
                                String.valueOf(maxPrio)),
                        message);
            }
        }
    }

    public void publish(String channel, int groupId, int priorityId, String message) {
        publish(Channel.get(channel), groupId, priorityId, message);
    }

    public void publish(Channel channel, int groupId, String message) {
        publish(channel, groupId, Integer.MAX_VALUE, message);
    }

    public void publish(String channel, int groupId, String message) {
        publish(Channel.get(channel), groupId, message);
    }

    public void publish(Channel channel, String message) {
        for (Integer grpId : channel.getSubscribes().keySet()) {
            publish(channel, grpId, message);
        }
    }

    public void publish(String channel, String message) {
        publish(Channel.get(channel), message);
    }
}