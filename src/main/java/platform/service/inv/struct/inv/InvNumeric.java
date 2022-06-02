package platform.service.inv.struct.inv;

public class InvNumeric implements Inv{
    private double min;
    private double max;

    public InvNumeric() {
        min = 0;
        max = 0;
    }

    @Override
    public boolean isViolated(double value) {
        return value < min || value > max;
    }
}
