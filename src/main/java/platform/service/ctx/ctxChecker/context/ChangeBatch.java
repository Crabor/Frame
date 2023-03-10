package platform.service.ctx.ctxChecker.context;

import java.util.List;

public class ChangeBatch {
    private final ChangeBatchType changeBatchType;
    private List<ContextChange> changeList;

    public ChangeBatch(ChangeBatchType changeBatchType) {
        this.changeBatchType = changeBatchType;
    }


    public ChangeBatchType getBatchType() {
        return changeBatchType;
    }


    public void setChangeList(List<ContextChange> changeList) {
        this.changeList = changeList;
    }

    public List<ContextChange> getChangeList() {
        return changeList;
    }
}
