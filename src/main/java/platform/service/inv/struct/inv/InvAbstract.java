package platform.service.inv.struct.inv;

import platform.service.inv.struct.CheckState;
import platform.service.inv.struct.InvState;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class InvAbstract{
    protected final String invDir = "output/inv/";
    protected String invFileName;
    String appName;
    int lineNumber;
    String varName;
    int group;
    List<Integer> trace;
    List<Integer> violatedTrace;
    InvState state;
    public void setMetaData(String appName, int lineNumber, int group, String varName, List<Integer> trace) {
        this.appName = appName;
        this.lineNumber = lineNumber;
        this.varName = varName;
        this.group = group;
        this.trace = trace;
        this.violatedTrace = new ArrayList<>();
        this.state = InvState.TRACE_COLLECT;
        this.invFileName = invDir + appName + "-" + "line" + lineNumber + "-" + "grp" + group + ".inv";
        File dir = new File(invDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public void addViolatedIter(int iterId) {
        if (violatedTrace == null) {
            violatedTrace = new ArrayList<>();
        }
        violatedTrace.add(iterId);
    }

    public void clearViolatedIters() {
        if (violatedTrace != null) {
            violatedTrace.clear();
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

    public List<Integer> getTrace() {
        return trace;
    }

    public List<Integer> getViolatedTrace() {
        return violatedTrace;
    }

    public InvState getState() {
        return state;
    }

    public void setState(InvState state) {
        this.state = state;
    }
    
    public void genInv() {
        File file = new File(invFileName);
        if (!file.exists()) {
            try {
                setInv();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            getInv();
        }
    }

    public abstract void setInv();

    public abstract void getInv();
}
