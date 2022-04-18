package platform.service.inv.struct;

import java.util.*;

public class SegInfo {
    public int iterId;
    public int gid;

    public List<Double> eCxt;
    public Set<Integer> pCxt;
    public Map<Integer, List<CheckInfo>> checkTable;

    public SegInfo(int iterId, List<Double> eCxt, Set<Integer> pCxt, Map<Integer, List<CheckInfo>> checkTable) {
        this.iterId = iterId;
        this.gid = 0;
        this.eCxt = eCxt;
        this.pCxt = pCxt;
        this.checkTable = checkTable;
    }

    public SegInfo(int iterId, List<Double> eCxt) {
        this.iterId = iterId;
        this.gid = 0;
        this.eCxt = eCxt;
        this.pCxt = new HashSet<>();
        this.checkTable = new HashMap<>();
    }

    public SegInfo(int iterId) {
        this.iterId = iterId;
        this.gid = 0;
        this.eCxt = new ArrayList<>();
        this.pCxt = new HashSet<>();
        this.checkTable = new HashMap<>();
    }
}