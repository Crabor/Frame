package platform.service.ctx.ctxServer;


import platform.service.ctx.ctxChecker.context.ChangeBatch;
import platform.service.ctx.item.ItemState;
import platform.service.ctx.pattern.Pattern;
import platform.service.ctx.pattern.types.DataSourceType;
import platform.service.ctx.pattern.types.FreshnessType;
import platform.service.ctx.ctxChecker.context.Context;
import platform.service.ctx.ctxChecker.context.ContextChange;

import java.util.*;

import static platform.service.ctx.ctxChecker.context.ChangeBatchType.GENERATE;
import static platform.service.ctx.ctxChecker.context.ChangeBatchType.OVERDUE;

public class ChangeGenerator {

    private final AbstractCtxServer ctxServer;
    private final PriorityQueue<Map.Entry<Long, Map.Entry<String, Context>>> activateContextsTimeQue;
    private final HashMap<String, LinkedList<Context>> activateContextsNumberMap;

    public ChangeGenerator(AbstractCtxServer ctxServer){
        this.ctxServer = ctxServer;
        this.activateContextsTimeQue = new PriorityQueue<>(50, (o1, o2) -> (int) (o1.getKey() - o2.getKey()));
        this.activateContextsNumberMap = new HashMap<>();
        initActivateContextsNumberMap(ctxServer.getPatternManager().getPatternMap());
    }

    private void initActivateContextsNumberMap(HashMap<String, Pattern> patternHashMap){
        for(Pattern pattern : patternHashMap.values()){
            if(pattern.getFreshnessType() == FreshnessType.NUMBER){
                activateContextsNumberMap.put(pattern.getPatternId(), new LinkedList<>());
            }
        }
    }

    public List<ChangeBatch> generateChangeBatches(Context context){
        List<ChangeBatch> changeBatchList = new ArrayList<>();
        //根据当前时间清理过时的contexts，生成相应的changes
        List<ContextChange> overdueList = new ArrayList<>();
        cleanOverdueContexts(overdueList);
        if(!overdueList.isEmpty()){
            ChangeBatch overdueChangeBatch = new ChangeBatch(OVERDUE);
            overdueChangeBatch.setChangeList(overdueList);
            changeBatchList.add(overdueChangeBatch);
        }

        assert context != null;
        String contextId = context.getContextId();

        List<ContextChange> generateList = new ArrayList<>();
        boolean matched = false;
        String fromSensorName = contextId.substring(0, contextId.lastIndexOf("_"));
        for(Pattern pattern : ctxServer.getPatternManager().getPatternMap().values()){
            assert pattern.getDataSourceType() != DataSourceType.PATTERN; // TODO()
            if(pattern.getDataSourceSet().contains(fromSensorName)){
                if(pattern.getMatcher() == null || match(pattern, context)){
                    matched = true;
                    generateList.addAll(generate(pattern, context));
                }
            }
        }
        if(!matched){
            ctxServer.getItemManager().addValidatedItem(Long.parseLong(contextId.split("_")[1]), context);
        }
        else{
            ChangeBatch generateChangeBatch = new ChangeBatch(GENERATE);
            generateChangeBatch.setChangeList(generateList);
            changeBatchList.add(generateChangeBatch);
        }

        return changeBatchList;
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
        if(pattern.getFreshnessType() == FreshnessType.NUMBER){
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
        if(pattern.getFreshnessType() == FreshnessType.NUMBER){
            LinkedList<Context> linkedList = activateContextsNumberMap.get(pattern.getPatternId());
            linkedList.offerLast(context);
        }
        else if(pattern.getFreshnessType() == FreshnessType.TIME){
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
                // update item state
                ctxServer.getItemManager().updateItemState(Long.parseLong(ctxId.split("_")[1]), ItemState.DROPPED);

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
                // update item state
                ctxServer.getItemManager().updateItemState(Long.parseLong(ctxId.split("_")[1]), ItemState.FIXED);
                assert false;
            }
        }

        return changeList;
    }


    public void reset(){
        activateContextsTimeQue.clear();
        activateContextsNumberMap.clear();
        initActivateContextsNumberMap(ctxServer.patternManager.getPatternMap());
    }

}
