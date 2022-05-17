package platform.service.inv.grptracefile;

import platform.service.inv.struct.SegInfo;

import java.util.List;
import java.util.Map;

public interface GrpTrace {
    void printGrpTraces(String appName, Map<Integer, SegInfo> segs, Map<Integer, List<Integer>> grps);
    void close();
}