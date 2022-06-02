package platform.service.inv.struct.inv;

import java.util.Collections;
import java.util.List;

public class InvNumeric implements Inv{
    private double min;
    private double max;

    public InvNumeric() {
        min = 0;
        max = 0;
    }

    public void genInv(List<Double> trace) {
        min = Collections.min(trace);
        max = Collections.max(trace);
    }

    @Override
    public boolean isViolated(double value) {
        //TODO:仅为演示
        return value < min;
//        return value < min || value > max;
    }

    @Override
    public String toString() {
        return "{" + min + "~" + max + '}';
    }
}
