package platform.service.cxt.Config;

import com.alibaba.fastjson.JSONObject;
import platform.service.inv.struct.trace.Trace;
import platform.service.inv.struct.trace.TraceCSV;
import platform.service.inv.struct.trace.TraceDIG;
import platform.service.inv.struct.trace.TraceDaikon;
import platform.struct.*;

public class CancerServerConfig {
    private boolean serverOn;
    private int groupThro;
    private int kMeansGroupSize;
    private double dosThro;
    private Trace groupTraceType;
    private InvGenMode invGenMode;
    private InvGenType invGenType;

    public CancerServerConfig(JSONObject object) {
        this.serverOn = object.getBoolean("serverOn");
        this.groupThro = object.getIntValue("groupThro");
        this.kMeansGroupSize = object.getIntValue("kMeansGroupSize");
        this.dosThro = object.getDoubleValue("dosThro");
        String gtt = object.getString("groupTraceType").toLowerCase();
        if (gtt.equals("csv")) {
            groupTraceType = new TraceCSV("csv");
        } else if (gtt.equals("daikon")) {
            groupTraceType = new TraceDaikon("daikon");
        } else if (gtt.equals("dig")) {
            groupTraceType = new TraceDIG("dig");
        }
        String igm = object.getString("invGenMode").toLowerCase();
        if (igm.equals("total")) {
            invGenMode = InvGenMode.TOTAL;
        } else if (igm.equals("incr")) {
            invGenMode = InvGenMode.INCR;
        }
        String igt = object.getString("invGenType").toLowerCase();
        if (igt.equals("numeric")) {
            invGenType = InvGenType.NUMERIC;
        }
    }

    public InvGenMode getInvGenMode() {
        return invGenMode;
    }

    public InvGenType getInvGenType() {
        return invGenType;
    }

    public Trace getGroupTraceType() {
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

    public boolean getServerOn() {
        return serverOn;
    }

    @Override
    public String toString() {
        return "CancerServerConfig{" +
                "serverOn=" + serverOn +
                ", groupThro=" + groupThro +
                ", kMeansGroupSize=" + kMeansGroupSize +
                ", dosThro=" + dosThro +
                ", groupTraceType=" + groupTraceType +
                ", invGenMode=" + invGenMode +
                ", invGenType=" + invGenType +
                '}';
    }
}
