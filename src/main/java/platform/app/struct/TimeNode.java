package platform.app.struct;

import java.util.ArrayList;
import java.util.List;

import static platform.app.struct.TimeLine.MAX_LEVEL;

public class TimeNode {
    public long time;
    public List<String> appGrpIds;
    public TimeNode[] forwards = new TimeNode[MAX_LEVEL];
    public int maxLevel;

    public TimeNode(long time, String appGrpId) {
        this.time = time;
        this.appGrpIds = new ArrayList<>();
        this.appGrpIds.add(appGrpId);
    }

    public TimeNode() {}

    @Override
    public String toString() {
        return String.format("%d:%s", time, appGrpIds);
    }
}
