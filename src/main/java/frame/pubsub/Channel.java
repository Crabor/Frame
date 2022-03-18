package frame.pubsub;

import frame.struct.GrpPrioPair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Channel {
    private Map<Integer, Map<Integer, List<AbstractSubscriber>>> subscribers;
    private final String channelBaseName;
    private static Map<String, Channel> objs;

    public static final int DEFAULT_GRP_ID = 0;
    public static final int DEFAULT_PRIO_ID = 0;
    public static final int GRP_ID_NOT_EXIST = Integer.MIN_VALUE;
    public static final int PRIO_ID_NOT_EXIST = Integer.MIN_VALUE;

    public Channel(String name) {
        channelBaseName = name;
        objs.put(name, this);
    }
    
    public static Channel getChannel(String channelName) {
        return objs.getOrDefault(channelName, new Channel(channelName));
    }

    public Map<Integer, Map<Integer, List<AbstractSubscriber>>> getSubscribers() {
        return subscribers;
    }

    public Map<Integer, List<AbstractSubscriber>> getGroup(int groupId) {
        return subscribers.get(groupId);
    }

    public int genNewGroupId() {
        int max = DEFAULT_GRP_ID;
        for (Integer grpId : subscribers.keySet()) {
            max = Math.max(max, grpId);
        }
        return max + 1;
    }

    public String getName() {
        return channelBaseName;
    }
    
    public int getGroupId(AbstractSubscriber s) {
        for (Map.Entry<Integer, Map<Integer, List<AbstractSubscriber>>> entry : subscribers.entrySet()) {
            Integer grpId = entry.getKey();
            Map<Integer, List<AbstractSubscriber>> grp = entry.getValue();
            for (Map.Entry<Integer, List<AbstractSubscriber>> e : grp.entrySet()) {
                List<AbstractSubscriber> subs = e.getValue();
                if (subs.contains(s)) {
                    return grpId;
                }
            }
        }
        return GRP_ID_NOT_EXIST;
    }
    
    public static int getGroupId(Channel c, AbstractSubscriber s) {
        return c.getGroupId(s);
    }

    public static int getGroupId(String c, AbstractSubscriber s) {
        return getChannel(c).getGroupId(s);
    }
    
    public int getPriorityId(AbstractSubscriber s) {
        for (Map.Entry<Integer, Map<Integer, List<AbstractSubscriber>>> entry : subscribers.entrySet()) {
            Map<Integer, List<AbstractSubscriber>> grp = entry.getValue();
            for (Map.Entry<Integer, List<AbstractSubscriber>> e : grp.entrySet()) {
                Integer prioId = entry.getKey();
                List<AbstractSubscriber> subs = e.getValue();
                if (subs.contains(s)) {
                    return prioId;
                }
            }
        }
        return PRIO_ID_NOT_EXIST;
    }

    public static int getPriorityId(Channel c, AbstractSubscriber s) {
        return c.getPriorityId(s);
    }

    public static int getPriorityId(String c, AbstractSubscriber s) {
        return getChannel(c).getPriorityId(s);
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
        return "Channel{" +
                "subscribers=" + subscribers +
                ", channelBaseName='" + channelBaseName + '\'' +
                '}';
    }
}
