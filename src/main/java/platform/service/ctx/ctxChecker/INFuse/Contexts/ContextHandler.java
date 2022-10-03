package platform.service.ctx.ctxChecker.INFuse.Contexts;


import java.util.ArrayList;
import java.util.List;

public class ContextHandler {

    public List<ContextChange> convertContextChanges(List<platform.service.ctx.Contexts.ContextChange> platChanges){
        List<ContextChange> changeList = new ArrayList<>();
        for(platform.service.ctx.Contexts.ContextChange platChange : platChanges){
            changeList.add(convertSingleChange(platChange));
        }
        return changeList;
    }

    private ContextChange convertSingleChange(platform.service.ctx.Contexts.ContextChange platChange){
        ContextChange engineChange = new ContextChange();
        engineChange.setChange_type(ContextChange.Change_Type.valueOf(platChange.getChangeType().toString()));
        engineChange.setPattern_id(platChange.getPatternId());
        engineChange.setContext(convertContext(platChange.getContext()));
        return engineChange;
    }

    private Context convertContext(platform.service.ctx.Contexts.Context platContext){
        Context engineContext = new Context();
        engineContext.setCtx_id(platContext.getContextId());
        engineContext.getCtx_fields().putAll(platContext.getContextFields());
        return engineContext;
    }
}
