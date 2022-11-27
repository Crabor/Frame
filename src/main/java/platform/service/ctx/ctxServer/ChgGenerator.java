package platform.service.ctx.ctxServer;


import platform.service.ctx.message.MessageHandler;
import platform.service.ctx.pattern.Pattern;
import platform.service.ctx.pattern.types.DataSourceType;
import platform.service.ctx.pattern.types.FreshnessType;
import platform.service.ctx.ctxChecker.context.Context;
import platform.service.ctx.ctxChecker.context.ContextChange;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class ChgGenerator implements Runnable {
    private Thread t;

    private final AbstractCtxServer server;
    private final PriorityBlockingQueue<Map.Entry<Long, Map.Entry<String, Context>>> activateContextsTimeQue;
    private final ConcurrentHashMap<String, LinkedBlockingQueue<Context>> activateContextsNumberMap;

    public ChgGenerator(AbstractCtxServer server){
        this.server = server;
        this.activateContextsTimeQue = new PriorityBlockingQueue<>(50, (o1, o2) -> (int) (o1.getKey() - o2.getKey()));
        this.activateContextsNumberMap = new ConcurrentHashMap<>();
        initActivateContextsNumberMap(server.getPatternMap());
    }

    private void initActivateContextsNumberMap(HashMap<String, Pattern> patternHashMap){
        for(Pattern pattern : patternHashMap.values()){
            if(pattern.getFreshnessType() == FreshnessType.number){
                activateContextsNumberMap.put(pattern.getPatternId(), new LinkedBlockingQueue<>());
            }
        }
    }

    public synchronized void generateChanges(Map<String, Context> contextMap){
        List<ContextChange> changeList = new ArrayList<>();
        //根据当前时间清理过时的contexts，生成相应的changes
        cleanOverdueContexts(changeList);

        if(contextMap != null){
            //为message中的每一个context寻找对应的patterns，并生成相应的changes
            for(String contextId : contextMap.keySet()){
                Context context = contextMap.get(contextId);
                if(context == null){
                    //由于被丢弃或者没获得，所以在生成Message的时候被设置为了null
                    server.getCtxFixer().addFixedContext(contextId, null);
                }
                else{
                    boolean matched = false;
                    String fromSensorName = contextId.substring(0, contextId.lastIndexOf("_"));
                    for(Pattern pattern : server.getPatternMap().values()){
                        if(pattern.getDataSourceType() == DataSourceType.pattern){
                            assert false;
                            continue;
                            //TODO()
                        }
                        if(pattern.getDataSourceSet().contains(fromSensorName)){
                            if(pattern.getMatcher() == null || match(pattern, context)){
                                matched = true;
                                changeList.addAll(generate(pattern, context));
                            }
                        }
                    }
                    if(!matched){
                        server.getCtxFixer().addFixedContext(contextId, MessageHandler.cloneContext(contextMap.get(contextId)));
                    }
                }
            }
        }

        //将changes写入buffer
        server.changeBufferProducer(changeList);
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
            LinkedBlockingQueue<Context> queue = activateContextsNumberMap.get(pattern.getPatternId());
            if(queue.size() == Integer.parseInt(pattern.getFreshnessValue())){
                Context oldContext = queue.poll();
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
            LinkedBlockingQueue<Context> queue = activateContextsNumberMap.get(pattern.getPatternId());
            queue.add(context);
        }
        else if(pattern.getFreshnessType() == FreshnessType.time){
            long overdueTime = new Date().getTime() + Long.parseLong(pattern.getFreshnessValue());
            activateContextsTimeQue.add(new AbstractMap.SimpleEntry<>(overdueTime, new AbstractMap.SimpleEntry<>(pattern.getPatternId(), context)));
        }

        return changeList;
    }

    @Override
    public void run() {
        while(true){
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                break;
            }
            generateChanges(null);
        }
    }

    public void start(){
        if (t == null) {
            t = new Thread(this, getClass().getName());
            t.start();
        }
    }

    public void reset(){
        t.interrupt();
        while(t.isInterrupted());
        generateChanges(null); // To ensure the last generateChanges invocation is done
        activateContextsTimeQue.clear();
        activateContextsNumberMap.clear();
    }


    public void restart(){
        t = new Thread(this, getClass().getName());
        t.start();
    }
}
