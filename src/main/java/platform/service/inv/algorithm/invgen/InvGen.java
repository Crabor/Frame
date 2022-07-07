package platform.service.inv.algorithm.invgen;

import platform.service.inv.struct.inv.InvAbstract;

import java.util.Map;

public interface InvGen {
    void run();
    Map<String, Map<String, Map<Integer, Map<Integer, InvAbstract>>>> getInvMap();
}
