package platform.service.inv.struct;

import java.util.Map;

public class InvData {
    double[] envCtxVals;
    Map<String, Double> checkVals;

    public InvData(double[] envCtxVals, Map<String, Double> checkVals) {
        this.envCtxVals = envCtxVals;
        this.checkVals = checkVals;
    }
}
