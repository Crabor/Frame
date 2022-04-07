package platform.pubsub;

import platform.struct.GrpPrioPair;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import reactor.util.annotation.Nullable;

import java.util.*;

public abstract class AbstractSubscriber implements RedisPubSubListener<String, String>, Subscriber {
    private static final List<AbstractSubscriber> objs = new ArrayList<>();
    private final Map<Channel, GrpPrioPair> channels = new HashMap<>();
    protected Publisher publisher = new Publisher();
    private static RedisClient client = null;
    protected Runnable thread = null;
    private StatefulRedisPubSubConnection<String, String> conn = null;

    public static void Init(RedisClient client) {
        AbstractSubscriber.client = client;
    }

    public static void Close() {
        objs.forEach(AbstractSubscriber::close);
    }

    public void close() {
        if (conn != null) {
            conn.close();
        }
    }

    public AbstractSubscriber() {
        conn = client.connectPubSub();
        objs.add(this);
    }

    public void bind(Runnable thread) {
        this.thread = thread;
    }

    @Nullable
    public static AbstractSubscriber getSubscriber(String name) {
        return objs.stream().filter(s -> s.getClass().getSimpleName().equals(name)).findFirst().orElse(null);
    }

    @Nullable
    public GrpPrioPair getGrpPrioPair(Channel channel) {
        return channels.get(channel);
    }

    @Nullable
    public GrpPrioPair getGrpPrioPair(String channel) {
        return getGrpPrioPair(Channel.getChannel(channel));
    }

    protected void publish(Channel channel, int groupId, int priorityId, String message) {
        publisher.publish(channel, groupId, priorityId, message);
    }

    protected void publish(String channel, int groupId, int priorityId, String message) {
        publisher.publish(channel, groupId, priorityId, message);
    }

    protected void publish(Channel channel, int groupId, String message) {
        publisher.publish(channel, groupId, message);
    }

    protected void publish(String channel, int groupId, String message) {
        publisher.publish(channel, groupId, message);
    }

    protected void publish(Channel channel, String message) {
        publisher.publish(channel, message);
    }

    protected void publish(String channel, String message) {
        publisher.publish(channel, message);
    }

    public void subscribe(Channel channel, int groupId, int priorityId) {
        channels.put(channel, channel.addSubscriber(this, groupId, priorityId));

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
        GrpPrioPair pair = channel.addSubscriber(this, groupId);
        channels.put(channel, pair);

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
        GrpPrioPair pair = channel.addSubscriber(this);
        channels.put(channel, pair);

        conn.addListener(this);
        RedisPubSubCommands<String, String> sync = conn.sync();
        sync.subscribe(
                String.join(
                        "-",
                        channel.getName(),
                        String.valueOf(pair.groupId),
                        String.valueOf(pair.priorityId)));
    }

    public void subscribe(String channel) {
        subscribe(Channel.getChannel(channel));
    }

    @Override
    public void message(String s, String s2) {
        onMessage(s.substring(0, s.indexOf('-')), s2);
    }

    @Override
    public void subscribed(String s, long l) {
        onSubscribed(s.substring(0, s.indexOf('-')), l);
    }

    @Override
    public void unsubscribed(String s, long l) {
        onUnsubscribed(s.substring(0, s.indexOf('-')), l);
    }

    @Override
    public void message(String s, String k1, String s2) {

    }

    @Override
    public void psubscribed(String s, long l) {

    }

    @Override
    public void punsubscribed(String s, long l) {

    }
}
