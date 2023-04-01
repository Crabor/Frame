package platform.service.inv.struct;

import common.util.Util;
import daikon.Daikon;

import java.io.*;
import java.util.*;

public class InvGroup {
    public List<InvData> datas;
    public final double[] centroid;
    public double radius;
    public Inv inv = null;

    public String appName;
    public int lineId;
    public int grpId;

    public InvGroup(int len, String appName, int lineId, int grpId) {
        datas = new ArrayList<>();
        centroid = new double[len];

        this.appName = appName;
        this.lineId = lineId;
        this.grpId = grpId;
    }

    public void invGen() {
        //TODO
        File dir = new File("output/" + appName + "/inv/" + lineId);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        //trace output
        String declFileName = dir.getAbsolutePath() + "/" + grpId + ".decl";
        File declFile = new File(declFileName);
        if (!declFile.exists()) {
            BufferedWriter out = null;
            try {
                out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(declFile, true)));
                out.write("DECLARE\n" + appName + "-" + lineId + "-" + grpId + ":::POINT\n");
                for (Map.Entry<String, Double> entry : datas.get(0).checkVals.entrySet()) {
                    String k = entry.getKey();
                    try {
                        out.write(k + "\ndouble\ndouble\n1\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                out.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        String varFileName = dir.getAbsolutePath() + "/" + grpId + ".dtrace";
        File varFile = new File(varFileName);
        if (!varFile.exists()) {
            BufferedWriter out = null;
            try {
                out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(varFile, true)));
                out.write(appName + "-" + lineId + "-" + grpId + ":::POINT\n");
                for (InvData data : datas) {
                    for (Map.Entry<String, Double> entry : data.checkVals.entrySet()) {
                        String k = entry.getKey();
                        Double v = entry.getValue();
                        try {
                            out.write(k + "\n" + v + "\n1\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    out.write("\n");
                }
                out.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        //inv gen
        String[] daikonArgs = new String[] {
                varFileName,
                declFileName,
                "--no_text_output",
                "--config",
                "Resources/configFile/platform/inv/config.txt",
        };
        daikon.Daikon.main(daikonArgs);
        inv = new InvDaikon(Daikon.all_ppts.get(appName + "-" + lineId + "-" + grpId + ":::POINT").getInvariants());
    }

    public static InvGroup makeGroup(String appName, int lineId, int grpId, InvData... datas) {
        int len = datas[0].envCtxVals.length;
        InvGroup ret = new InvGroup(len, appName, lineId, grpId);
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
