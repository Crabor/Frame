package frame.pubsub;

import frame.struct.GrpPrioPair;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;

import java.util.*;

public abstract class AbstractSubscriber implements RedisPubSubListener {
    private static RedisClient client;
    private static List<AbstractSubscriber> objs = new ArrayList<>();
    private StatefulRedisPubSubConnection<String, String> conn;
    private Set<Channel> channels = new HashSet<>();
    private Runnable thread;

    public static void Init(RedisClient client) {
        AbstractSubscriber.client = client;
    }

    public static void Close() {
        objs.forEach(AbstractSubscriber::close);
    }

    public void close() {
        conn.close();
    }

    public void bind(Runnable thread) {
        this.thread = thread;
    }

    public AbstractSubscriber() {
        conn = client.connectPubSub();
        objs.add(this);
    }

    public AbstractSubscriber(Channel... channels) {
        this();
        for (Channel channel : channels) {
            subscribe(channel);
        }
    }

    public AbstractSubscriber(String... channels) {
        this();
        for (String channel : channels) {
            subscribe(channel);
        }
    }

    public void subscribe(Channel channel, int groupId, int priorityId) {
        this.channels.add(channel);
        channel.addSubscriber(this, groupId, priorityId);
        conn.addListener(this);
        RedisPubSubCommands<String, String> sync = conn.sync();
        sync.subscribe(
                String.join(
                        "-", 
                        channel.getName(), 
                        String.valueOf(groupId), 
                        String.valueOf(priorityId)));
    }

    public void subscribe(String channel, int groupId, int priorityId) {
        subscribe(Channel.getChannel(channel), groupId, priorityId);
    }

    public void subscribe(Channel channel, int groupId) {
        this.channels.add(channel);
        GrpPrioPair pair = channel.addSubscriber(this, groupId);
        RedisPubSubCommands<String, String> sync = conn.sync();
        sync.subscribe(String.join(
                "-", channel.getName(),
                String.valueOf(groupId), String.valueOf(pair.priorityId)));
    }

    public void subscribe(String channel, int groupId) {
        subscribe(Channel.getChannel(channel), groupId);
    }

    public void subscribe(Channel channel) {
        this.channels.add(channel);
        GrpPrioPair pair = channel.addSubscriber(this);
        RedisPubSubCommands<String, String> sync = conn.sync();
        sync.subscribe(String.join(
                "-", channel.getName(),
                String.valueOf(pair.groupId), String.valueOf(pair.priorityId)));
    }

    public void subscribe(String channel) {
        subscribe(Channel.getChannel(channel));
    }
}
