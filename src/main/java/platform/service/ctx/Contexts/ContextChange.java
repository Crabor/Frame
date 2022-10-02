package platform.service.ctx.Contexts;

public class ContextChange {
    public enum ChangeType {ADDITION, DELETION}

    private ChangeType changeType;
    private String patternId;
    private Context context;

    public void setPatternId(String patternId) {
        this.patternId = patternId;
    }

    public void setChangeType(ChangeType changeType) {
        this.changeType = changeType;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    public Context getContext() {
        return context;
    }

    public String getPatternId() {
        return patternId;
    }

    @Override
    public String toString() {
        return "<" + (changeType == ChangeType.ADDITION ? "+" : "-") + ", " + patternId + ", " + context.getContextId() + ">";
    }
}
