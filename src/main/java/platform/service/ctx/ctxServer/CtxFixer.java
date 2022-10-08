package platform.service.ctx.ctxServer;

import platform.service.ctx.ctxChecker.constraint.runtime.Link;
import platform.service.ctx.ctxChecker.context.Context;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class CtxFixer {
    private final AbstractCtxServer ctxServer;
    private final HashMap<String, Set<String>> ctxId2IncRuleIdSet;

    private final LinkedBlockingQueue<Context> fixedContextQue;

    public CtxFixer(AbstractCtxServer ctxServer) {
        this.ctxServer = ctxServer;
        this.ctxId2IncRuleIdSet = new HashMap<>();
        this.fixedContextQue = new LinkedBlockingQueue<>();
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
            fixedContextQue.add(context);
        }


    }

    public LinkedBlockingQueue<Context> getFixedContextQue() {
        return fixedContextQue;
    }
}
