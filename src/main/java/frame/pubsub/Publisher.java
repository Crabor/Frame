package frame.pubsub;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Publisher {
    private static RedisClient client;
    private static final List<Publisher> objs = new ArrayList<>();
    private final StatefulRedisConnection<String, String> conn;

    public static void Init(RedisClient client) {
        Publisher.client = client;
    }

    public Publisher() {
        conn = client.connect();
        objs.add(this);
    }

    public static void Close() {
        objs.forEach(Publisher::close);
    }

    public void close() {
        conn.close();
    }

    public void publish(Channel channel, int groupId, int priorityId, String message) {
        RedisCommands<String, String> commands = conn.sync();
        Map<Integer, List<AbstractSubscriber>> grp = channel.getGroup(groupId);
        if (grp != null) {
            int maxPrio = Integer.MIN_VALUE;
            for (Integer prio : grp.keySet()) {
                if (prio > priorityId) {
                    continue;
                }
                maxPrio = Math.max(maxPrio, prio);
            }
            if (maxPrio != Integer.MIN_VALUE) {
                String realChannel = String.join("-", channel.getName(), String.valueOf(groupId), String.valueOf(maxPrio));
                commands.publish(realChannel, message);
            }
        }
    }

    public void publish(String channel, int groupId, int priorityId, String message) {
        publish(Channel.getChannel(channel), groupId, priorityId, message);
    }

    public void publish(Channel channel, int groupId, String message) {
        publish(channel, groupId, Integer.MAX_VALUE, message);
    }

    public void publish(String channel, int groupId, String message) {
        publish(Channel.getChannel(channel), groupId, message);
    }

    public void publish(Channel channel, String message) {
        for (Integer grpId : channel.getSubscribers().keySet()) {
            publish(channel, grpId, message);
        }
    }

    public void publish(String channel, String message) {
        publish(Channel.getChannel(channel), message);
    }
}
