package platform.service.inv.struct.trace;

import platform.service.inv.struct.SegInfo;

import java.util.List;
import java.util.Map;

public interface Trace {
    void printTrace(String appName, int lineNumber, int gid, Map<Integer, SegInfo> segs, List<Integer> trace);
}
