package platform.app.struct;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TimeLine {
    public static final int MAX_LEVEL = 16;
    private int levelCount = 1;
    private TimeNode head = new TimeNode();
    //第一维为appGrpId，第二维为freq
    private Map<Integer, Integer> appGrpId2Freq = new HashMap<>();

    public Map<Integer, Integer> getAppGrpId2Freq() {
        return appGrpId2Freq;
    }

    public TimeNode getHead() {
        return head;
    }

    public TimeNode find(long time) {
        TimeNode p = head;
        for (int i = levelCount - 1; i >= 0; i--) {
            while (p.forwards[i] != null && p.forwards[i].time < time) {
                p = p.forwards[i];
            }
        }
        if (p.forwards[0] != null && p.forwards[0].time == time) {
            return p.forwards[0];
        } else {
            return null;
        }
    }

    public int size() {
        int size = 0;
        TimeNode p = head;
        while (p.forwards[0] != null) {
            size ++;
            p = p.forwards[0];
        }
        return size;
    }

    public void insert(long time, String appGrpId) {
        TimeNode node = find(time);
        if (node != null) {
            node.appGrpIds.add(appGrpId);
        } else {
            int level = randomLevel();
            TimeNode newNode = new TimeNode(time, appGrpId);
            newNode.maxLevel = level;

            TimeNode[] update = new TimeNode[level];
            for (int i = 0; i < level; i++) {
                update[i] = head;
            }

            TimeNode p = head;
            for (int i = level - 1; i >= 0; i--) {
                while (p.forwards[i] != null && p.forwards[i].time < time) {
                    p = p.forwards[i];
                }
                update[i] = p;
            }

            for (int i = 0; i < level; i++) {
                newNode.forwards[i] = update[i].forwards[i];
                update[i].forwards[i] = newNode;
            }

            if (levelCount < level) {
                levelCount = level;
            }
        }
    }

    public void delete(long time, String appGrpId) {
        TimeNode node = find(time);
        if (node != null) {
            node.appGrpIds.remove(appGrpId);
            if (node.appGrpIds.isEmpty()) {
                TimeNode[] update = new TimeNode[levelCount];
                TimeNode p = head;
                for (int i = levelCount - 1; i >= 0; i--) {
                    while (p.forwards[i] != null && p.forwards[i].time < time) {
                        p = p.forwards[i];
                    }
                    update[i] = p;
                }

                if (p.forwards[0] != null && p.forwards[0].time == time) {
                    for (int i = levelCount - 1; i >= 0; i--) {
                        if (update[i].forwards[i] != null && update[i].forwards[i].time == time) {
                            update[i].forwards[i] = update[i].forwards[i].forwards[i];
                        }
                    }
                }
            }
        }
    }

    public void deleteAppGrpId(int appGrpId, int freq) {
        if (appGrpId2Freq.containsKey(appGrpId)) {
            double sleepTime = 1000 / (double) freq;
            for (int i = 1; i <= freq; i++) {
                long time = Math.round(sleepTime * i);
                delete(time, String.valueOf(appGrpId));
            }
            appGrpId2Freq.remove(appGrpId);
        }
    }

    public void insertAppGrpId(int appGrpId, int freq) {
        if (appGrpId2Freq.containsKey(appGrpId)) {
            deleteAppGrpId(appGrpId, appGrpId2Freq.get(appGrpId));
        }
        double sleepTime = 1000 / (double) freq;
        for (int i = 1; i <= freq; i++) {
            long time = Math.round(sleepTime * i);
            insert(time, String.valueOf(appGrpId));
        }
    }

    private int randomLevel() {
        int level = 1;
        Random random = new Random();
        while (random.nextDouble() < 0.5 && level < MAX_LEVEL) {
            level++;
        }
        return level;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        TimeNode p = head.forwards[0];
        while (p != null) {
            sb.append(p);
            if (p.forwards[0] != null) {
                sb.append(" -> ");
            }
            p = p.forwards[0];
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        TimeLine timeline = new TimeLine();
        timeline.insert(1000, "1");
        timeline.insert(3000, "2");
        timeline.insert(2000, "3");
        timeline.insert(500, "4");
        System.out.println(timeline.size());
        timeline.insert(500, "5");
        System.out.println(timeline);
        System.out.println(timeline.size());
        timeline.delete(500, "6");
        System.out.println(timeline);
        System.out.println(timeline.size());
//        double a = 1000 / 3.0;
//        System.out.println(a);
//        double b = a * 3;
//        System.out.println(b);
//        double c = 1000 - b;
//        System.out.println(c == 0);
    }
}
