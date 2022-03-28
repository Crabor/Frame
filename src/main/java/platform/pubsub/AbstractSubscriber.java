package platform.pubsub;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import platform.struct.GrpPrioPair;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;

import java.util.*;

public abstract class AbstractSubscriber implements RedisPubSubListener<String, String>, InterfaceSubscriber {
    private static final List<AbstractSubscriber> objs = new ArrayList<>();
    private final Set<Channel> channels = new HashSet<>();
    protected Publisher publisher = new Publisher();
    protected Runnable thread = null;
    private static RedisClient client = null;
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

    public void bind(Runnable thread) {
        this.thread = thread;
    }

    public AbstractSubscriber() {
        conn = client.connectPubSub();
        objs.add(this);
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
