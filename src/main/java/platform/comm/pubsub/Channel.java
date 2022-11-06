package platform.comm.pubsub;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import platform.struct.GrpPrioMode;
import reactor.util.annotation.Nullable;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Channel {
    private final Map<Integer, Map<Integer, Subscribe>> subscribes = new HashMap<>();
    private final String channelBaseName;
    private static final Map<String, Channel> objs = new HashMap<>();
    private static final Lock objsLock = new ReentrantLock();
    private final Log logger = LogFactory.getLog(Channel.class);

    public static final int DEFAULT_GRP_ID = 0;
    public static final int DEFAULT_PRIO_ID = 0;
    public static final long DEFAULT_MODE = 0;

    public Channel(String name) {
        channelBaseName = name;
        objsLock.lock();
        objs.put(name, this);
        objsLock.unlock();
    }

    public static Channel get(String channelName) {
        Channel ret = objs.get(channelName);
        if (ret == null) {
            ret = new Channel(channelName);
        }
        return ret;
    }

    public static Collection<Channel> getObjs() {
        return objs.values();
    }

    public Map<Integer, Map<Integer, Subscribe>> getSubscribes() {
        return subscribes;
    }

    public Map<Integer, Subscribe> getSubscribes(int groupId) {
        return subscribes.get(groupId);
    }

    public int genNewGroupId() {
        int max = DEFAULT_GRP_ID;
        if (subscribes.isEmpty()) {
            return max;
        }
        for (Integer grpId : subscribes.keySet()) {
            max = Math.max(max, grpId);
        }
        return max + 1;
    }

    public String getName() {
        return channelBaseName;
    }
    
    @Nullable
    public GrpPrioMode getGrpPrioMode(AbstractSubscriber s) {
        for (Map.Entry<Integer, Map<Integer, Subscribe>> entry : subscribes.entrySet()) {
            int grpId = entry.getKey();
            Map<Integer, Subscribe> grp = entry.getValue();
            for (Map.Entry<Integer, Subscribe> e : grp.entrySet()) {
                int prioId = e.getKey();
                Subscribe sub = e.getValue();
                if (sub.contains(s)) {
                    return new GrpPrioMode(grpId, prioId, sub.getMode());
                }
            }
        }
        return null;
    }

    public static GrpPrioMode getGrpPrioMode(Channel c, AbstractSubscriber s) {
        return c.getGrpPrioMode(s);
    }

    public static GrpPrioMode getGrpPrioMode(String c, AbstractSubscriber s) {
        return get(c).getGrpPrioMode(s);
    }

    public GrpPrioMode addSubscribe(AbstractSubscriber subscriber, int groupId, int priorityId, long mode) {
        GrpPrioMode gpm = new GrpPrioMode(groupId, priorityId, mode);
        if (!subscribes.containsKey(groupId)) {
            subscribes.put(groupId, new HashMap<>());
        }
        Map<Integer, Subscribe> grp = subscribes.get(groupId);
        if (!grp.containsKey(priorityId)) {
            grp.put(priorityId, Subscribe.get(this, gpm));
        }
        Subscribe sub = grp.get(priorityId);
        sub.addSubscriber(subscriber);
        return gpm;
    }
    
    public GrpPrioMode addSubscribe(AbstractSubscriber subscriber, int groupId, int priorityId) {
        return addSubscribe(subscriber, groupId, priorityId, DEFAULT_MODE);
    }

    public GrpPrioMode addSubscribe(AbstractSubscriber subscriber, int groupId) {
        return addSubscribe(subscriber, groupId, DEFAULT_PRIO_ID, DEFAULT_MODE);
    }

    public GrpPrioMode addSubscribe(AbstractSubscriber subscriber) {
        int groupId = genNewGroupId();
        return addSubscribe(subscriber, groupId, DEFAULT_PRIO_ID, DEFAULT_MODE);
    }

    @Override
    public String toString() {
        return "Channel{" +
                "channelBaseName='" + channelBaseName + '\'' +
                ", subscribes=" + subscribes +
                '}';
    }
}
