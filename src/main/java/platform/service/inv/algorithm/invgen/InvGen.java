package platform.service.inv.algorithm.invgen;

import platform.service.inv.struct.inv.Inv;

import java.util.Map;

public interface InvGen {
    void readTraces(String traceDir);
    Map<String, Map<String, Map<Integer, Map<Integer, Inv>>>> invGen();
}
