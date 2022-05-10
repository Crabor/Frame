package platform.service.cxt.Config;

import com.alibaba.fastjson.JSONObject;

public class CancerServerConfig {
    private int groupThro;
    private int kMeansGroupSize;
    private double dosThro;

    public CancerServerConfig(JSONObject object) {
        this.groupThro = object.getIntValue("groupThro");
        this.kMeansGroupSize = object.getIntValue("kMeansGroupSize");
        this.dosThro = object.getDoubleValue("dosThro");
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
                '}';
    }
}
