package platform.service.ctx.ctxServiceFrame;

import platform.service.ctx.ctxChecker.constraints.runtime.Link;
import platform.service.ctx.ctxChecker.contexts.Context;
import platform.service.ctx.ctxServiceFrame.ctxServer.AbstractCtxServer;

import java.util.*;

public class CtxFixer {
    private final AbstractCtxServer ctxServer;
    private final HashMap<String, Set<String>> ctxId2IncRuleIdSet;

    public CtxFixer(AbstractCtxServer ctxServer) {
        this.ctxServer = ctxServer;
        this.ctxId2IncRuleIdSet = new HashMap<>();
    }

    public void filterInconsistencies(Map<String, Set<Link>> ruleId2LinkSet){
        for(String ruleId : ruleId2LinkSet.keySet()){
            String variable = ctxServer.getResolverMap().get(ruleId).getVariable();
            for(Link link : ruleId2LinkSet.get(ruleId)){
                for(Map.Entry<String, Context> va : link.getVaSet()){
                    if(va.getKey().equals(variable)){
                        ctxId2IncRuleIdSet.computeIfAbsent(va.getValue().getContextId(), k -> new HashSet<>());
                        Objects.requireNonNull(ctxId2IncRuleIdSet.computeIfPresent(va.getValue().getContextId(), (k, v) -> v)).add(ruleId);
                    }
                }
            }
        }
    }

    public void fixContext(Context context){
        if(!ctxId2IncRuleIdSet.containsKey(context.getContextId())){

        }


    }
}
