package platform.service.ctx.ctxServer;

import platform.service.ctx.ctxChecker.constraint.runtime.Link;
import platform.service.ctx.ctxChecker.context.Context;
import platform.service.ctx.ctxChecker.context.ContextChange;
import platform.service.ctx.message.Message;
import platform.service.ctx.rule.Rule;
import platform.service.ctx.rule.resolver.AbstractResolver;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CtxFixer {
    private final AbstractCtxServer ctxServer;

    private final ConcurrentHashMap<Long, Message> validatedMsgMap;

    // delay resolve
    private final HashMap<String, Set<Map.Entry<String, Link>>> ctxId2Incs;


    public CtxFixer(AbstractCtxServer ctxServer) {
        this.ctxServer = ctxServer;
        this.ctxId2Incs = new HashMap<>();
        this.validatedMsgMap = new ConcurrentHashMap<>();
    }

    public void buildValidatedMessage(long msgIndex, Context context){
        Message validatedMsg = new Message(msgIndex);
        validatedMsg.addContext(context);
        ctxServer.serverStatistics.increaseCheckedAndResolvedMsgNum();
        validatedMsgMap.put(msgIndex, validatedMsg);
    }

    public ConcurrentHashMap<Long, Message> getValidatedMsgMap() {
        return validatedMsgMap;
    }

    public void reset(){
        this.ctxId2Incs.clear();
        this.validatedMsgMap.clear();
    }


    // in time resolving
    public List<ContextChange> resolveViolationsInTime(Map<String, Set<Link>> ruleId2LinkSet){
        if(ruleId2LinkSet.isEmpty()){
            return new ArrayList<>();
        }
        //每次检测只检测由一个context引起的changeList，故只需统计优先级最高的resolver
        String selectedRuleId = null;
        AbstractResolver selectedResolver = null;
        for(String ruleId : ruleId2LinkSet.keySet()){
            AbstractResolver resolver = ctxServer.getResolverMap().get(ruleId);
            if(selectedResolver == null){
                selectedRuleId = ruleId;
                selectedResolver = resolver;
            }
            else{
                if(selectedResolver.getPriority() < resolver.getPriority()) {
                    selectedRuleId = ruleId;
                    selectedResolver = resolver;
                }
            }
        }
        //将selectedRule相关的LinkSet变成通用容器
        Set<HashMap<String, Map.Entry<String, HashMap<String, String>>>> flatLinkSet = flattenLinkSet(ctxServer.getRuleMap().get(selectedRuleId), ruleId2LinkSet.get(selectedRuleId));
        //resolve
        Set<Map.Entry<String, HashMap<String, String>>> resolvedFlatContextSet = selectedResolver.resolve(flatLinkSet);
        //生成对应的resolveChangeBatch
        return ctxServer.changeGenerator.generateResolveChangeBatch(resolvedFlatContextSet);
    }

    private Set<HashMap<String, Map.Entry<String, HashMap<String, String>>>> flattenLinkSet(Rule rule, Set<Link> linkSet){
        Set<HashMap<String, Map.Entry<String, HashMap<String, String>>>> convertedLinkSet = new HashSet<>();
        for(Link link : linkSet){
            HashMap<String, Map.Entry<String, HashMap<String, String>>> flatLink = new HashMap<>();
            for(Map.Entry<String, Context> va : link.getVaSet()){
                String var = va.getKey();
                Context context = va.getValue();
                HashMap<String, String> ctxFields = new HashMap<>();
                for(String fieldName : context.getContextFields().keySet()){
                    ctxFields.put(fieldName, context.getContextFields().get(fieldName));
                }
                flatLink.put(rule.getVarPatternMap().get(var), new AbstractMap.SimpleEntry<>(context.getContextId(), ctxFields));
            }
            convertedLinkSet.add(flatLink);
        }
        return convertedLinkSet;
    }


    // delay resolve
    public void storeInconsistenciesForDelayResolving(Map<String, Set<Link>> ruleId2LinkSet){
        for(String ruleId : ruleId2LinkSet.keySet()){
            ctxServer.getServerStatistics().addLinks(ruleId, ruleId2LinkSet.get(ruleId));
            for(Link link : ruleId2LinkSet.get(ruleId)){
                Map.Entry<String, Link> ruleLinkPair = new AbstractMap.SimpleEntry<>(ruleId, link);
                for(Map.Entry<String, Context> va : link.getVaSet()){
                    ctxId2Incs.putIfAbsent(va.getValue().getContextId(), new HashSet<>());
                    ctxId2Incs.get(va.getValue().getContextId()).add(ruleLinkPair);
                }
            }
        }
    }

    public void fixContext(Context context){
        //TODO
        /*
        if(!ctxId2Incs.containsKey(context.getContextId())){
            addFixedContext(context.getContextId(), MessageHandler.cloneContext(context));
            return;
        }

        String contextId = context.getContextId();
        ResolverType resolverType = null;
        Map<String, String> fixingPairs = null;
        for(Map.Entry<String, Link> ruleLinkPair : ctxId2Incs.get(contextId)){
            String ruleId = ruleLinkPair.getKey();
            Link link = ruleLinkPair.getValue();
            String resolveVar = ctxServer.getResolverMap().get(ruleId).getVariable();
            ResolverType tmpType = ctxServer.getResolverMap().get(ruleId).getResolverType();
            for(Map.Entry<String, Context> va : link.getVaSet()){
                if(va.getKey().equals(resolveVar) && va.getValue().getContextId().equals(contextId)){
                    resolverType = resolverType == ResolverType.drop ? ResolverType.drop : tmpType;
                    fixingPairs = resolverType == ResolverType.fix ? ctxServer.getResolverMap().get(ruleId).getFixingPairs() : null;

                    ctxServer.getServerStatistics().increaseProblematicCtxNum(ctxServer.getRuleMap().get(ruleId).getVarPatternMap().get(resolveVar));
                }
            }
        }

        //remove side effect
        if(resolverType != null){
            for(Map.Entry<String, Link> ruleLinkPair : ctxId2Incs.get(contextId)){
                Iterator<String> iterator = ctxId2Incs.keySet().iterator();
                while(iterator.hasNext()){
                    String ctxId = iterator.next();
                    if(ctxId.equals(contextId)) continue;
                    ctxId2Incs.get(ctxId).remove(ruleLinkPair);
                    if(ctxId2Incs.get(ctxId).isEmpty()){
                        iterator.remove();
                    }
                }
            }
        }
        ctxId2Incs.remove(contextId);

        if(resolverType == null){
            addFixedContext(context.getContextId(), MessageHandler.cloneContext(context));
        }
        else if(resolverType == ResolverType.drop){
            addFixedContext(contextId, null);
        }
        else if(resolverType == ResolverType.fix){
            addFixedContext(contextId, MessageHandler.fixAndCloneContext(context, fixingPairs));
        }
        //TODO: maybe other resolverTypes
         */
    }

}
