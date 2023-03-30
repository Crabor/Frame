package platform.service.inv.struct;

import common.util.Util;

import java.util.*;

public class InvGroup {
    public List<InvData> datas;
    public final double[] centroid;
    public double radius;
    public Inv inv = null;

    public InvGroup(int len) {
        datas = new ArrayList<>();
        centroid = new double[len];
    }

    public void invGen() {
        //TODO
    }

    public static InvGroup makeGroup(InvData... datas) {
        int len = datas[0].envCtxVals.length;
        InvGroup ret = new InvGroup(len);
        ret.datas.addAll(Arrays.asList(datas));
        for (InvData data : datas) {
            for (int i = 0; i < len; i++) {
                ret.centroid[i] += data.envCtxVals[i];
            }
        }
        for (int i = 0; i < len; i++) {
            ret.centroid[i] /= datas.length;
        }
        double maxDist = 0;
        for (InvData data : datas) {
            double dist = Util.distance(ret.centroid, data.envCtxVals);
            if (dist > maxDist) {
                maxDist = dist;
            }
        }
        ret.radius = maxDist;
        return ret;
    }

    public void add(InvData... newDatas) {
        int oldSize = datas.size();
        datas.addAll(Arrays.asList(newDatas));
        for (int i = 0; i < centroid.length; i++) {
            centroid[i] = centroid[i] * oldSize;
        }
        for (InvData data : newDatas) {
            for (int i = 0; i < centroid.length; i++) {
                centroid[i] += data.envCtxVals[i];
            }
        }
        for (int i = 0; i < centroid.length; i++) {
            centroid[i] /= datas.size();
        }
        double maxDist = 0;
        for (InvData data : datas) {
            double dist = Util.distance(centroid, data.envCtxVals);
            if (dist > maxDist) {
                maxDist = dist;
            }
        }
        radius = maxDist;
    }

    public int size() {
        return datas.size();
    }
}
