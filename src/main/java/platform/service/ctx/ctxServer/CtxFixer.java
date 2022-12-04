package platform.service.ctx.ctxServer;

import platform.service.ctx.ctxChecker.constraint.runtime.Link;
import platform.service.ctx.ctxChecker.context.Context;
import platform.service.ctx.message.Message;
import platform.service.ctx.message.MessageHandler;
import platform.service.ctx.rule.resolver.ResolverType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CtxFixer {
    private final AbstractCtxServer ctxServer;
    private final HashMap<String, Set<String>> ctxId2IncRuleIdSet;
    private final TreeMap<Long, Message> fixingMsgMap;
    private final ConcurrentHashMap<Long, Message> sendingMsgMap;

    public CtxFixer(AbstractCtxServer ctxServer) {
        this.ctxServer = ctxServer;
        this.ctxId2IncRuleIdSet = new HashMap<>();
        this.fixingMsgMap = new TreeMap<>(Long::compareTo);
        this.sendingMsgMap = new ConcurrentHashMap<>();
    }

    public void filterInconsistencies(Map<String, Set<Link>> ruleId2LinkSet){
        for(String ruleId : ruleId2LinkSet.keySet()){
            ctxServer.getServerStatistics().addLinks(ruleId, ruleId2LinkSet.get(ruleId));
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

            String variable = ctxServer.getResolverMap().get(ruleId).getVariable();
            ctxServer.getServerStatistics().increaseProblematicCtxNum(ctxServer.getRuleMap().get(ruleId).getVarPatternMap().get(variable));
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
        long msgIndex =  Long.parseLong(contextId.substring(contextId.lastIndexOf("_") + 1));
        Message fixingMsg = getOrPutDefaultFixingMsg(msgIndex);
        fixingMsg.addContext(contextId, context);
        if(completenessChecking(fixingMsg)){
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

    private boolean completenessChecking(Message fixingMsg){
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
        this.ctxId2IncRuleIdSet.clear();
        this.fixingMsgMap.clear();
        this.sendingMsgMap.clear();
    }

}
