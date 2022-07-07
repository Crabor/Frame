package platform.service.inv.struct.inv;

import platform.service.inv.struct.CheckState;
import platform.service.inv.struct.InvState;

import java.util.ArrayList;
import java.util.List;

public abstract class InvAbstract{
    String appName;
    int lineNumber;
    String varName;
    int group;
    List<Integer> iters;
    List<Integer> violatedIters;
    InvState state;

    public void addViolatedIter(int iterId) {
        if (violatedIters == null) {
            violatedIters = new ArrayList<>();
        }
        violatedIters.add(iterId);
    }

    public void clearViolatedIters() {
        if (violatedIters != null) {
            violatedIters.clear();
        }
    }

    public abstract boolean isViolated(double value);

    public String getAppName() {
        return appName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getVarName() {
        return varName;
    }

    public int getGroup() {
        return group;
    }

    public List<Integer> getIters() {
        return iters;
    }

    public List<Integer> getViolatedIters() {
        return violatedIters;
    }

    public InvState getState() {
        return state;
    }

    public void setState(InvState state) {
        this.state = state;
    }
}
