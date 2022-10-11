package platform.service.ctx.ctxServer;

import platform.config.CtxServerConfig;
import platform.service.ctx.ctxChecker.constraint.runtime.Link;
import platform.service.ctx.ctxChecker.context.Context;
import platform.service.ctx.message.MessageHandler;
import platform.service.ctx.rule.resolver.Resolver;
import platform.service.ctx.rule.resolver.ResolverType;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class CtxFixer {
    private final AbstractCtxServer ctxServer;
    private final HashMap<String, Set<String>> ctxId2IncRuleIdSet;

    private final LinkedBlockingQueue<Map.Entry<String, Context>> fixedContextQue;

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
                        ctxId2IncRuleIdSet.get(va.getValue().getContextId()).add(ruleId);
                    }
                }
            }
        }
    }

    public void fixContext(Context context){
        if(!ctxId2IncRuleIdSet.containsKey(context.getContextId())){
            addFixedContext(context.getContextId(), MessageHandler.cloneContext(context));
            return;
        }
        String contextId = context.getContextId();
        ResolverType resolverType = null;
        Map<String, String> fixingPairs = null;
        for(String ruleId : ctxId2IncRuleIdSet.get(contextId)){
            ResolverType tmpType = ctxServer.getResolverMap().get(ruleId).getResolverType();
            if(resolverType != ResolverType.drop){
                resolverType = tmpType;
            }
            if(resolverType == ResolverType.fix){
                fixingPairs = ctxServer.getResolverMap().get(ruleId).getFixingPairs();
            }
        }
        if(resolverType == ResolverType.drop){
            addFixedContext(contextId, null);
        }
        else if(resolverType == ResolverType.fix){
            addFixedContext(contextId, MessageHandler.fixAndCloneContext(context, fixingPairs));
        }
        //TODO: maybe other resolverTypes
    }

    public void addFixedContext(String contextId, Context context){
        fixedContextQue.add(new AbstractMap.SimpleEntry<>(contextId, context));
    }

    public LinkedBlockingQueue<Map.Entry<String, Context>> getFixedContextQue() {
        return fixedContextQue;
    }
}
