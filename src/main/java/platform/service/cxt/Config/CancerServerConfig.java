package platform.service.cxt.Config;

import com.alibaba.fastjson.JSONObject;
import platform.service.inv.algorithm.invgen.InvGen;
import platform.service.inv.algorithm.invgen.InvGenNumeric;
import platform.service.inv.struct.grptracefile.GrpTrace;
import platform.service.inv.struct.grptracefile.GrpTraceCSV;
import platform.service.inv.struct.grptracefile.GrpTraceDIG;
import platform.service.inv.struct.grptracefile.GrpTraceDaikon;
import platform.struct.*;

public class CancerServerConfig {
    private int groupThro;
    private int kMeansGroupSize;
    private double dosThro;
    private GrpTrace groupTraceType;
    private InvGenMode invGenMode;
    private InvGen invGenType;

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
        String igm = object.getString("invGenMode").toLowerCase();
        if (igm.equals("total")) {
            invGenMode = InvGenMode.TOTAL;
        } else if (igm.equals("incr")) {
            invGenMode = InvGenMode.INCR;
        }
        String igt = object.getString("invGenType").toLowerCase();
        if (igt.equals("numeric")) {
            invGenType = new InvGenNumeric("csv");
        }
    }

    public InvGenMode getInvGenMode() {
        return invGenMode;
    }

    public InvGen getInvGenType() {
        return invGenType;
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
                ", invGenMode=" + (invGenMode == 1 ? "incr" : "total") +
                ", invGenType=" + invGenType +
                '}';
    }
}
