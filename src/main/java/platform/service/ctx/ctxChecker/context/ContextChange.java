package platform.service.ctx.ctxChecker.context;

public class ContextChange {

    public enum ChangeType {ADDITION, DELETION, UPDATE};

    private ChangeType changeType;
    private String patternId;
    private Context context;

    //setter and getter
    public void setChangeType(ChangeType changeType) {
        this.changeType = changeType;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setPatternId(String patternId) {
        this.patternId = patternId;
    }

    public Context getContext() {
        return context;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    public String getPatternId() {
        return patternId;
    }

    @Override
    public String toString() {
        return "<" + (changeType == ChangeType.ADDITION ? "+" : "-") + ", " + patternId + ", " + context.getContextId() + ">";
    }
}
