package platform.service.ctx.ctxServer;

import platform.service.ctx.ctxChecker.constraint.runtime.Link;
import platform.service.ctx.ctxChecker.context.Context;
import platform.service.ctx.ctxChecker.context.ContextChange;
import platform.service.ctx.message.Message;
import platform.service.ctx.message.MessageHandler;
import platform.service.ctx.rule.Rule;
import platform.service.ctx.rule.resolver.AbstractResolver;
import platform.service.ctx.rule.resolver.ResolverType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CtxFixer {
    private final AbstractCtxServer ctxServer;

    private final TreeMap<Long, Message> fixingMsgMap;
    private final ConcurrentHashMap<Long, Message> sendingMsgMap;

    // in time resolve

    // delay resolve
    private final HashMap<String, Set<Map.Entry<String, Link>>> ctxId2Incs;


    public CtxFixer(AbstractCtxServer ctxServer) {
        this.ctxServer = ctxServer;
        this.ctxId2Incs = new HashMap<>();
        this.fixingMsgMap = new TreeMap<>(Long::compareTo);
        this.sendingMsgMap = new ConcurrentHashMap<>();
    }

    public void addFixedContext(String contextId, Context context){
        long msgIndex =  Long.parseLong(contextId.substring(contextId.lastIndexOf("_") + 1));
        Message fixingMsg = getOrPutDefaultFixingMsg(msgIndex);
        fixingMsg.addContext(contextId, context);
        if(checkCompleteness(fixingMsg)){
            ctxServer.serverStatistics.increaseCheckedAndResolvedMsgNum();
            sendingMsgMap.put(msgIndex, fixingMsg);
            fixingMsgMap.remove(msgIndex);
        }
    }

    private Message getOrPutDefaultFixingMsg(long index){
        Message message = this.fixingMsgMap.getOrDefault(index, new Message(index));
        this.fixingMsgMap.put(index, message);
        return message;
    }

    private boolean checkCompleteness(Message fixingMsg){
        Message originalMsg = ctxServer.getOriginalMsg(fixingMsg.getIndex());
        //查看是否这条信息的所有context都已收齐
        Set<String> originalMsgContextIds = originalMsg.getContextMap().keySet();
        Set<String> fixingMsgContextIds = fixingMsg.getContextMap().keySet();
        return originalMsgContextIds.containsAll(fixingMsgContextIds) && fixingMsgContextIds.containsAll(originalMsgContextIds);
    }

    public ConcurrentHashMap<Long, Message> getSendingMsgMap() {
        return sendingMsgMap;
    }

    public void reset(){
        this.ctxId2Incs.clear();
        this.fixingMsgMap.clear();
        this.sendingMsgMap.clear();
    }


    // in time resolve
    public List<ContextChange> resolveInconsistenciesInTime(Map<String, Set<Link>> ruleId2LinkSet){
        //每次检测只检测由一个context引起的changeList，故只需统计优先级最高的resolver
        String topRuleId = null;
        AbstractResolver topResolver = null;
        for(String ruleId : ruleId2LinkSet.keySet()){
            AbstractResolver resolver = ctxServer.getResolverMap().get(ruleId);
            if(topResolver == null){
                topRuleId = ruleId;
                topResolver = resolver;
            }
            else{
                if(topResolver.getPriority() < resolver.getPriority()) {
                    topRuleId = ruleId;
                    topResolver = resolver;
                }
            }
        }

        Set<HashMap<String, Map.Entry<String, HashMap<String, String>>>> flatLinkSet = flattenLinkSet(ctxServer.getRuleMap().get(topRuleId), ruleId2LinkSet.get(topRuleId));

        Set<Map.Entry<String, HashMap<String, String>>> resolvedFlatContextSet = topResolver.resolve(flatLinkSet);

        return ctxServer.chgGenerator.generateResolvedChanges(resolvedFlatContextSet);
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
