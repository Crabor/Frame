package platform.service.inv.struct;

import java.util.*;

public class SegInfo {
    public int iterId;
    public int gid;
    public Map<String, Double> eCxt;
    public Set<Integer> pCxt;
    public Map<Integer, List<CheckInfo>> checkTable;

    public SegInfo(int iterId, Map<String, Double> eCxt, Set<Integer> pCxt, Map<Integer, List<CheckInfo>> checkTable) {
        this.iterId = iterId;
        this.gid = 0;
        this.eCxt = eCxt;
        this.pCxt = pCxt;
        this.checkTable = checkTable;
    }

    public SegInfo(int iterId) {
        this.iterId = iterId;
        this.gid = 0;
        this.eCxt = new HashMap<>();
        this.pCxt = new HashSet<>();
        this.checkTable = new HashMap<>();
    }

    @Override
    public String toString() {
        return "SegInfo{" +
                "iterId=" + iterId +
                ", gid=" + gid +
                ", eCxt=" + eCxt +
                ", pCxt=" + pCxt +
                ", checkTable=" +
                '}';
    }
}