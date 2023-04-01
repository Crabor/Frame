package platform.service.inv.struct;

import common.struct.InvServiceConfig;
import common.struct.enumeration.CheckResult;
import common.util.Util;

import java.util.ArrayList;
import java.util.List;

public class InvLine {
    InvServiceConfig config;
    String[] checkNames;
    List<InvGroup> invGroups;
    InvGroup newGroup;

    String appName;
    int lindId;

    public InvLine(InvServiceConfig config ,String[] checkNames, String appName, int lindId) {
        this.config = config;
        this.checkNames = checkNames;
        this.invGroups = new ArrayList<>();
        this.newGroup = null;

        this.appName = appName;
        this.lindId = lindId;
    }

    public String[] getCheckNames() {
        return checkNames;
    }

    public CheckResult check(InvData data) {
        CheckResult ret = CheckResult.INV_GENERATING;
        if (newGroup != null && newGroup.size() < config.getInitThro()) {
            newGroup.add(data);
        } else {
            newGroup = null;
            double minDist = Double.MAX_VALUE;
            InvGroup nearestGroup = null;

            for (InvGroup group : invGroups) {
                double dist = Util.distance(group.centroid, data.envCtxVals);
                if (dist < minDist && dist <= group.radius) {
                    minDist = dist;
                    nearestGroup = group;
                }
            }

            if (nearestGroup == null) {
                newGroup = InvGroup.makeGroup(appName, lindId,  invGroups.size() + 1, data);
                invGroups.add(newGroup);
            } else {
                if (nearestGroup.size() == config.getGenThro()) {
                    if (nearestGroup.inv != null) {
                        ret = nearestGroup.inv.check(data);
                    }
                } else {
                    nearestGroup.add(data);
                    if (nearestGroup.size() == config.getGenThro()) {
                        nearestGroup.invGen();
                    }
                }
            }
        }
        return ret;
    }
}
