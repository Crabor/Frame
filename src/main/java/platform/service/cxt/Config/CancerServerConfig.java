package platform.service.cxt.Config;

import com.alibaba.fastjson.JSONObject;
import platform.service.inv.struct.grptracefile.GrpTrace;
import platform.service.inv.struct.grptracefile.GrpTraceCSV;
import platform.service.inv.struct.grptracefile.GrpTraceDIG;
import platform.service.inv.struct.grptracefile.GrpTraceDaikon;

public class CancerServerConfig {
    private int groupThro;
    private int kMeansGroupSize;
    private double dosThro;
    private GrpTrace groupTraceType;

    public CancerServerConfig(JSONObject object) {
        this.groupThro = object.getIntValue("groupThro");
        this.kMeansGroupSize = object.getIntValue("kMeansGroupSize");
        this.dosThro = object.getDoubleValue("dosThro");
        String gtt = object.getString("groupTraceType").toLowerCase();
        if (gtt.equals("csv")) {
            groupTraceType = new GrpTraceCSV("csv");
        } else if (gtt.equals("daikon")) {
            groupTraceType = new GrpTraceDaikon("daikon");
        } else if (gtt.equals("dig")) {
            groupTraceType = new GrpTraceDIG("dig");
        }
    }

    public GrpTrace getGroupTraceType() {
        return groupTraceType;
    }

    public int getGroupThro() {
        return groupThro;
    }

    public int getKMeansGroupSize() {
        return kMeansGroupSize;
    }

    public double getDosThro() {
        return dosThro;
    }

    @Override
    public String toString() {
        return "CancerServerConfig{" +
                "groupThro=" + groupThro +
                ", kMeansGroupSize=" + kMeansGroupSize +
                ", dosThro=" + dosThro +
                ", groupTraceType=" + groupTraceType +
                '}';
    }
}
