package platform.pubsub;

import io.lettuce.core.api.StatefulRedisConnection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import platform.struct.GrpPrioMode;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import reactor.util.annotation.Nullable;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractSubscriber implements RedisPubSubListener<String, String>, Subscriber {
    private static final List<AbstractSubscriber> objs = new ArrayList<>();
    private static final Lock objsLock = new ReentrantLock();
    private final Map<Channel, Subscribe> subscribes = new HashMap<>();
    protected Publisher publisher;
    private static RedisClient client = null;

    protected static Log logger;
    private final StatefulRedisPubSubConnection<String, String> pubsubConn;
    protected final StatefulRedisConnection<String, String> commonConn;

    public static void Init(RedisClient client) {
        AbstractSubscriber.client = client;
    }

    public static void Close() {
        objs.forEach(AbstractSubscriber::close);
    }

    @Override
    public String toString() {
        return getClass().getName();
    }

    public String getName() {
        return toString();
    }

    public static List<AbstractSubscriber> getObjs() {
        return objs;
    }

    public void close() {
        if (pubsubConn != null) {
            pubsubConn.close();
        }
    }

    public AbstractSubscriber() {
        pubsubConn = client.connectPubSub();
        commonConn = client.connect();
        publisher = new Publisher();
        objsLock.lock();
        objs.add(this);
        objsLock.unlock();
        logger = LogFactory.getLog(this.getClass());
    }

    @Nullable
    public static AbstractSubscriber getSubscriber(String name) {
        return objs.stream().filter(s -> s.getName().equals(name)).findFirst().orElse(null);
    }

    @Nullable
    public GrpPrioMode getGrpPrioMode(Channel channel) {
        return subscribes.get(channel).getGrpPrioMode();
    }

    @Nullable
    public GrpPrioMode getGrpPrioMode(String channel) {
        return getGrpPrioMode(Channel.get(channel));
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

    private boolean addListenerFlag = false;

    private Subscribe _subscribe(Channel channel, GrpPrioMode gpm) {
        Subscribe sub = Subscribe.get(channel, gpm);
        sub.addSubscriber(this);
        subscribes.put(channel, sub);
        if (!addListenerFlag) {
            addListenerFlag = true;
            pubsubConn.addListener(this);
        }
        RedisPubSubCommands<String, String> sync = pubsubConn.sync();
        sync.subscribe(
                String.join(
                        "-",
                        channel.getName(),
                        String.valueOf(gpm.groupId),
                        String.valueOf(gpm.priorityId)));
        return sub;
    }

    public Subscribe subscribe(Channel channel, int groupId, int priorityId, long mode) {
        return _subscribe(channel, channel.addSubscribe(this, groupId, priorityId, mode));
    }

    public Subscribe subscribe(String channel, int groupId, int priorityId, long mode) {
        return subscribe(Channel.get(channel), groupId, priorityId, mode);
    }

    public Subscribe subscribe(Channel channel, int groupId, int priorityId) {
        return _subscribe(channel, channel.addSubscribe(this, groupId, priorityId));
    }

    public Subscribe subscribe(String channel, int groupId, int priorityId) {
        return subscribe(Channel.get(channel), groupId, priorityId);
    }

    public Subscribe subscribe(Channel channel, int groupId) {
        return _subscribe(channel, channel.addSubscribe(this, groupId));
    }

    public Subscribe subscribe(String channel, int groupId) {
        return subscribe(Channel.get(channel), groupId);
    }

    public Subscribe subscribe(Channel channel) {
        return _subscribe(channel, channel.addSubscribe(this));
    }

    public Subscribe subscribe(String channel) {
        return subscribe(Channel.get(channel));
    }

    @Override
    public void message(String s, String s2) {
        onMessage(s.substring(0, s.indexOf('-')), s2);
    }

    @Override
    public void subscribed(String s, long l) {

    }

    @Override
    public void unsubscribed(String s, long l) {

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
