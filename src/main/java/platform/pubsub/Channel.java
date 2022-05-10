package platform.pubsub;

import platform.struct.GrpPrioPair;
import reactor.util.annotation.Nullable;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Channel {
    private final Map<Integer, Map<Integer, List<AbstractSubscriber>>> subscribers = new HashMap<>();
    private final String channelBaseName;
    private static final Map<String, Channel> objs = new HashMap<>();
    private static final Lock objsLock = new ReentrantLock();

    public static final int DEFAULT_GRP_ID = 0;
    public static final int DEFAULT_PRIO_ID = 0;

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

    public Map<Integer, Map<Integer, List<AbstractSubscriber>>> getSubscribers() {
        return subscribers;
    }

    public Map<Integer, List<AbstractSubscriber>> getGroup(int groupId) {
        return subscribers.get(groupId);
    }

    public int genNewGroupId() {
        int max = DEFAULT_GRP_ID;
        if (subscribers.isEmpty()) {
            return max;
        }
        for (Integer grpId : subscribers.keySet()) {
            max = Math.max(max, grpId);
        }
        return max + 1;
    }

    public String getName() {
        return channelBaseName;
    }

    @Nullable
    public GrpPrioPair getGrpPrio(AbstractSubscriber s) {
        for (Map.Entry<Integer, Map<Integer, List<AbstractSubscriber>>> entry : subscribers.entrySet()) {
            int grpId = entry.getKey();
            Map<Integer, List<AbstractSubscriber>> grp = entry.getValue();
            for (Map.Entry<Integer, List<AbstractSubscriber>> e : grp.entrySet()) {
                int prioId = e.getKey();
                List<AbstractSubscriber> subs = e.getValue();
                if (subs.contains(s)) {
                    return new GrpPrioPair(grpId, prioId);
                }
            }
        }
        return null;
    }

    public static GrpPrioPair getGrpPrio(Channel c, AbstractSubscriber s) {
        return c.getGrpPrio(s);
    }

    public static GrpPrioPair getGrpPrio(String c, AbstractSubscriber s) {
        return get(c).getGrpPrio(s);
    }

    public GrpPrioPair addSubscriber(AbstractSubscriber subscriber, int groupId, int priorityId) {
        if (!subscribers.containsKey(groupId)) {
            subscribers.put(groupId, new HashMap<>());
        }
        Map<Integer, List<AbstractSubscriber>> grp = subscribers.get(groupId);
        if (!grp.containsKey(priorityId)) {
            grp.put(priorityId, new ArrayList<>());
        }
        List<AbstractSubscriber> prio = grp.get(priorityId);
        prio.add(subscriber);
        return new GrpPrioPair(groupId, priorityId);
    }

    public GrpPrioPair addSubscriber(AbstractSubscriber subscriber, int groupId) {
        addSubscriber(subscriber, groupId, DEFAULT_PRIO_ID);
        return new GrpPrioPair(groupId, DEFAULT_PRIO_ID);
    }

    public GrpPrioPair addSubscriber(AbstractSubscriber subscriber) {
        int groupId = genNewGroupId();
        addSubscriber(subscriber, groupId, DEFAULT_PRIO_ID);
        return new GrpPrioPair(groupId, DEFAULT_PRIO_ID);
    }

    @Override
    public String toString() {
        return "{" + channelBaseName + "=" + subscribers + "}";
    }
}
