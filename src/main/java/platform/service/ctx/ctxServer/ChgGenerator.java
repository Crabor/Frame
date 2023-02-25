package platform.service.ctx.ctxServer;


import platform.service.ctx.message.MessageHandler;
import platform.service.ctx.pattern.Pattern;
import platform.service.ctx.pattern.types.DataSourceType;
import platform.service.ctx.pattern.types.FreshnessType;
import platform.service.ctx.ctxChecker.context.Context;
import platform.service.ctx.ctxChecker.context.ContextChange;

import java.util.*;

import static platform.service.ctx.ctxServer.BatchType.GENERATE;
import static platform.service.ctx.ctxServer.BatchType.OVERDUE;

public class ChgGenerator {

    private final AbstractCtxServer server;
    private final PriorityQueue<Map.Entry<Long, Map.Entry<String, Context>>> activateContextsTimeQue;
    private final HashMap<String, LinkedList<Context>> activateContextsNumberMap;

    public ChgGenerator(AbstractCtxServer server){
        this.server = server;
        this.activateContextsTimeQue = new PriorityQueue<>(50, (o1, o2) -> (int) (o1.getKey() - o2.getKey()));
        this.activateContextsNumberMap = new HashMap<>();
        initActivateContextsNumberMap(server.getPatternMap());
    }

    private void initActivateContextsNumberMap(HashMap<String, Pattern> patternHashMap){
        for(Pattern pattern : patternHashMap.values()){
            if(pattern.getFreshnessType() == FreshnessType.number){
                activateContextsNumberMap.put(pattern.getPatternId(), new LinkedList<>());
            }
        }
    }

    public List<Map.Entry<List<ContextChange>, BatchType>> generateChangeBatches(Context context){
        List<Map.Entry<List<ContextChange>, BatchType>> retList = new ArrayList<>();
        //根据当前时间清理过时的contexts，生成相应的changes
        List<ContextChange> overdueList = new ArrayList<>();
        cleanOverdueContexts(overdueList);
        if(!overdueList.isEmpty()){
            retList.add(new AbstractMap.SimpleEntry<>(overdueList, OVERDUE));
        }

        assert context != null;
        String contextId = context.getContextId();

        List<ContextChange> generateList = new ArrayList<>();
        boolean matched = false;
        String fromSensorName = contextId.substring(0, contextId.lastIndexOf("_"));
        for(Pattern pattern : server.getPatternMap().values()){
            assert pattern.getDataSourceType() != DataSourceType.pattern; // TODO()
            if(pattern.getDataSourceSet().contains(fromSensorName)){
                if(pattern.getMatcher() == null || match(pattern, context)){
                    matched = true;
                    generateList.addAll(generate(pattern, context));
                }
            }
        }
        if(!matched){
            server.getCtxFixer().buildReadyMsg(Long.parseLong(contextId.split("_")[1]), MessageHandler.cloneContext(context));
        }
        else{
            retList.add(new AbstractMap.SimpleEntry<>(generateList, GENERATE));
        }

        return retList;
    }

    private void cleanOverdueContexts(List<ContextChange> changeList){
        long currentTime = new Date().getTime();
        while(!activateContextsTimeQue.isEmpty()){
            long overdueTime = activateContextsTimeQue.peek().getKey();
            String patternId = activateContextsTimeQue.peek().getValue().getKey();
            Context context = activateContextsTimeQue.peek().getValue().getValue();
            if(overdueTime <= currentTime){
                ContextChange delChange = new ContextChange();
                delChange.setChangeType(ContextChange.ChangeType.DELETION);
                delChange.setPatternId(patternId);
                delChange.setContext(context);
                //TODO(): inducing from-pattern changes.
                changeList.add(delChange);

                activateContextsTimeQue.poll();
            }
            else{
                break;
            }
        }
    }

    private boolean match(Pattern pattern, Context context){
        return pattern.getMatcher().match(context);
    }

    private List<ContextChange> generate(Pattern pattern, Context context){
        List<ContextChange> changeList = new ArrayList<>();
        //判断是否是number，如果是，判断是否满容量，如果是，先生成delChange，如果有delChange，则要考虑 inducing from-pattern changes.
        if(pattern.getFreshnessType() == FreshnessType.number){
            LinkedList<Context> linkedList = activateContextsNumberMap.get(pattern.getPatternId());
            if(linkedList.size() == Integer.parseInt(pattern.getFreshnessValue())){
                Context oldContext = linkedList.pollFirst();
                assert oldContext != null;
                ContextChange delChange = new ContextChange();
                delChange.setChangeType(ContextChange.ChangeType.DELETION);
                delChange.setPatternId(pattern.getPatternId());
                delChange.setContext(oldContext);
                changeList.add(delChange);
                //TODO(): inducing from-pattern changes.
            }
        }
        //生成addChange
        ContextChange addChange = new ContextChange();
        addChange.setChangeType(ContextChange.ChangeType.ADDITION);
        addChange.setPatternId(pattern.getPatternId());
        addChange.setContext(context);
        changeList.add(addChange);

        //更新activateContexts容器
        if(pattern.getFreshnessType() == FreshnessType.number){
            LinkedList<Context> linkedList = activateContextsNumberMap.get(pattern.getPatternId());
            linkedList.offerLast(context);
        }
        else if(pattern.getFreshnessType() == FreshnessType.time){
            long overdueTime = new Date().getTime() + Long.parseLong(pattern.getFreshnessValue());
            activateContextsTimeQue.add(new AbstractMap.SimpleEntry<>(overdueTime, new AbstractMap.SimpleEntry<>(pattern.getPatternId(), context)));
        }

        return changeList;
    }


    public List<ContextChange> generateResolveChangeBatch(Set<Map.Entry<String, HashMap<String, String>>> resolvedFlatContextSet){
        List<ContextChange> changeList = new ArrayList<>();
        for(Map.Entry<String, HashMap<String, String>> flatContext : resolvedFlatContextSet){
            String ctxId = flatContext.getKey();
            HashMap<String, String> ctx = flatContext.getValue();
            /*只存在两种情况：
            1. ctx == null：表示删除这一上下文，只需生成delete change
            2. ctx != null: 表示修改这一上下文，先生成原来的delete change,再生成add change
             */
            if(ctx == null){
                // time freshness
                Iterator<Map.Entry<Long, Map.Entry<String, Context>>> queIter = activateContextsTimeQue.iterator();
                while(queIter.hasNext()){
                    Map.Entry<Long, Map.Entry<String, Context>> entry = queIter.next();
                    if(entry.getValue().getValue().getContextId().equals(ctxId)){
                        //delete change
                        ContextChange delChange = new ContextChange();
                        delChange.setChangeType(ContextChange.ChangeType.DELETION);
                        delChange.setPatternId(entry.getValue().getKey());
                        delChange.setContext(entry.getValue().getValue());
                        changeList.add(delChange);

                        queIter.remove();
                    }
                }

                // number freshness
                for(String patternId : activateContextsNumberMap.keySet()){
                    Iterator<Context> listIter = activateContextsNumberMap.get(patternId).iterator();
                    while(listIter.hasNext()){
                        Context context = listIter.next();
                        if(context.getContextId().equals(ctxId)){
                            //delete change
                            ContextChange delChange = new ContextChange();
                            delChange.setChangeType(ContextChange.ChangeType.DELETION);
                            delChange.setPatternId(patternId);
                            delChange.setContext(context);
                            changeList.add(delChange);

                            listIter.remove();
                        }
                    }
                }
            }
            else{
                //TODO
                assert false;
            }
        }

        return changeList;
    }


    public void reset(){
        activateContextsTimeQue.clear();
        activateContextsNumberMap.clear();
        initActivateContextsNumberMap(server.getPatternMap());
    }

}
