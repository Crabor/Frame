package platform.service.inv.algorithm.tracegrp;


import platform.service.inv.struct.SegInfo;

import java.util.*;

public class DoS {
    double THRESHOLD;
    Map<Integer, SegInfo> segs;
    Map<Integer, List<Integer>> inGrps;
    Map<Integer, List<Integer>> outGrps;

    public DoS(Map<Integer, SegInfo> segs, Map<Integer, List<Integer>> inGrps, double thro) {
        this.segs = segs;
        this.inGrps = inGrps;
        this.outGrps = new HashMap<>();
        this.THRESHOLD = thro;
    }

    private double dos(SegInfo a, SegInfo b) {
        Set<Integer> inter = new HashSet<>(a.pCxt);
        inter.retainAll(b.pCxt);
        Set<Integer> union = new HashSet<>(a.pCxt);
        union.addAll(b.pCxt);
        double interSize = inter.size();
        double unionSize = union.size();
        return union.size() == 0 ? 1.0 : interSize / unionSize;
    }

    private int grouping(int grpIdLeft, List<Integer> inGrp) {
        int grpIdRight = grpIdLeft + 1;
        for (Integer iterIding : inGrp) {
            boolean like = false;
            int i;
            for (i = grpIdLeft; i < grpIdRight; i++) {
                List<Integer> outGrp = outGrps.get(i);
                if (outGrp == null) {
                    like = true;
                    break;
                } else {
                    int j;
                    for (j = 0; j < outGrp.size(); j++) {
                        int iterIded = outGrp.get(j);
                        if (dos(segs.get(iterIded), segs.get(iterIding)) < THRESHOLD) {
                            break;
                        }
                    }
                    if (j == outGrp.size()) {
                        like = true;
                        break;
                    }
                }
            }
            if (like) {
                segs.get(iterIding).gid = i;
                List<Integer> outGrp = outGrps.get(i);
                if (outGrp == null) {
                    outGrps.put(i, new ArrayList<>(List.of(iterIding)));
                } else {
                    outGrp.add(iterIding);
                }
            } else {
                segs.get(iterIding).gid = grpIdRight;
                outGrps.put(grpIdRight, new ArrayList<>(List.of(iterIding)));
                grpIdRight++;
            }
        }
        return grpIdRight;
    }

    public void run() {
        int grpId = 0;
        for (List<Integer> inGrp : inGrps.values()) {
            grpId = grouping(grpId, inGrp);
        }
    }

    public Map<Integer, List<Integer>> getOutGrps() {
        return outGrps;
    }
}
