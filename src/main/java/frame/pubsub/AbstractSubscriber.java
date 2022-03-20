package frame.pubsub;

import frame.struct.GrpPrioPair;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;

import java.util.*;

public abstract class AbstractSubscriber implements RedisPubSubListener<String, String> {
    private static RedisClient client;
    private static final List<AbstractSubscriber> objs = new ArrayList<>();
    private final StatefulRedisPubSubConnection<String, String> conn;
    private final Set<Channel> channels = new HashSet<>();
    private Runnable thread;
    protected Publisher publisher = new Publisher();

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

        conn.addListener(this);
        RedisPubSubCommands<String, String> sync = conn.sync();
        sync.subscribe(
                String.join(
                        "-",
                        channel.getName(),
                        String.valueOf(groupId),
                        String.valueOf(pair.priorityId)));
    }

    public void subscribe(String channel, int groupId) {
        subscribe(Channel.getChannel(channel), groupId);
    }

    public void subscribe(Channel channel) {
        this.channels.add(channel);
        GrpPrioPair pair = channel.addSubscriber(this);

        conn.addListener(this);
        RedisPubSubCommands<String, String> sync = conn.sync();
        String realChannel = String.join("-", channel.getName(), String.valueOf(pair.groupId), String.valueOf(pair.priorityId));
        sync.subscribe(realChannel);
    }

    public void subscribe(String channel) {
        subscribe(Channel.getChannel(channel));
    }
}
