package platform.comm.pubsub;

import io.lettuce.core.KeyValue;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import platform.config.SubConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

public class Subscribe implements Runnable {
    private Thread t;
    private static final Map<String, Subscribe> objs = new HashMap<>();
    private static final Lock objsLock = new ReentrantLock();
    private static RedisClient client = null;
    private final StatefulRedisConnection<String, String> conn;
    private final Log logger = LogFactory.getLog(Subscribe.class);

    private final String name;
    private final Channel channel;
    private int groupId;
    private int priorityId;
    private long mode;
    private final List<AbstractSubscriber> subscribers = new ArrayList<>();

    public static void Init(RedisClient client) {
        Subscribe.client = client;
    }

    public static void Close() {
        objs.values().forEach(Subscribe::close);
    }

    public void close() {
        conn.close();
    }

    private Subscribe(SubConfig config) {
        conn = client.connect();
        channel = Channel.get(config.channel);
        groupId = config.groupId;
        priorityId = config.priorityId;
        name = String.join(
                    "-",
                    config.channel,
                    String.valueOf(config.groupId),
                    String.valueOf(config.priorityId));
        mode = config.mode;
        objsLock.lock();
        objs.put(name, this);
        objsLock.unlock();
//        start();
    }

    public void addSubscriber(AbstractSubscriber subscriber) {
        subscribers.add(subscriber);
    }

    public static Subscribe get(SubConfig config) {
        Subscribe ret = objs.get(String.join(
                                    "-",
                                    config.channel,
                                    String.valueOf(config.groupId),
                                    String.valueOf(config.priorityId)));
        if (ret == null) {
            ret = new Subscribe(config);
        }
        return ret;
    }
    
    public static Subscribe get(String channel, GrpPrioMode gpm) {
        return get(new SubConfig(channel, gpm));
    }
    
    public static Subscribe get(Channel channel, GrpPrioMode gpm) {
        return get(new SubConfig(channel.getName(), gpm));
    }

    public boolean contains(AbstractSubscriber s) {
        return subscribers.contains(s);
    }

    public GrpPrioMode getGrpPrioMode() {
        return new GrpPrioMode(groupId, priorityId, mode);
    }

    public void start() {
        if (t == null) {
            t = new Thread(this, "Subscribe");
            t.start();
        }
    }

    @Override
    public void run() {
        RedisAsyncCommands<String, String> asyncCommands = conn.async();
        RedisCommands<String, String> syncCommands = conn.sync();
        while (true) {
            try {
                if (mode == -1) {
                    LockSupport.park();
                    asyncCommands.brpop(0, name).thenAccept(kv -> asyncCommands.publish(kv.getKey(), kv.getValue()));
                } else {
                    if (mode != 0) {
                        Thread.sleep(mode);
                    }
                    // TODO: 等待10s可优化
                    long timeOut = 10;
                    KeyValue<String, String> kv = syncCommands.brpop(timeOut, name);
                    if (kv != null) {
                        syncCommands.publish(name, kv.getValue());
                    } else {
                        logger.warn("FIFO pipeline \"" + name + "\" waited for " + timeOut + " seconds without any data");
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Thread getThread() {
        return t;
    }

    public Channel getChannel() {
        return channel;
    }

    public List<AbstractSubscriber> getSubscriber() {
        return subscribers;
    }

    public int getGroupId() {
        return groupId;
    }

    public int getPriorityId() {
        return priorityId;
    }

    public long getMode() {
        return mode;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public void setPriorityId(int priorityId) {
        this.priorityId = priorityId;
    }

    public void setMode(long mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        return "{mode=" + mode + ", subscribers=" + subscribers + '}';
    }
}
