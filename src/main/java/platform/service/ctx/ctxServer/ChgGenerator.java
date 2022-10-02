package platform.service.ctx.ctxServer;


import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import platform.service.ctx.Contexts.Context;
import platform.service.ctx.Contexts.ContextChange;
import platform.service.ctx.Messages.Message;
import platform.service.ctx.Patterns.FunctionMatcher;
import platform.service.ctx.Patterns.Pattern;
import platform.service.ctx.Patterns.PrimaryKeyMatcher;
import platform.service.ctx.Patterns.Types.DataSourceType;
import platform.service.ctx.Patterns.Types.FreshnessType;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    }

    public HashMap<String, Pattern> buildPatterns(String patternFile, String mfuncFile){
        Object mfuncInstance = loadMfuncFile(mfuncFile);

        HashMap<String, Pattern> patternHashMap = new HashMap<>();
        try {
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(new File(patternFile));
            List<Element> patternElements = document.getRootElement().elements();
            for(Element patternElement :  patternElements){
                List<Element> labelElements = patternElement.elements();
                assert labelElements.size() == 3 || labelElements.size() == 4;
                Pattern pattern = new Pattern();
                //patternId
                assert labelElements.get(0).getName().equals("id");
                pattern.setPatternId(labelElements.get(0).getText());
                //freshness
                assert labelElements.get(1).getName().equals("freshness");
                List<Element> freshnessElements = labelElements.get(1).elements();
                assert freshnessElements.size() == 2;
                assert freshnessElements.get(0).getName().equals("type");
                assert freshnessElements.get(1).getName().equals("value");
                pattern.setFreshnessType(FreshnessType.valueOf(freshnessElements.get(0).getText()));
                pattern.setFreshnessValue(freshnessElements.get(1).getText());
                //dataSource
                assert labelElements.get(2).getName().equals("dataSource");
                List<Element> dataSourceElements = labelElements.get(2).elements();
                assert dataSourceElements.size() == 2;
                assert dataSourceElements.get(0).getName().equals("type");
                assert dataSourceElements.get(1).getName().equals("sourceList");
                pattern.setDataSourceType(DataSourceType.valueOf(dataSourceElements.get(0).getText()));
                List<Element> sourceElements = dataSourceElements.get(1).elements();
                for(Element sourceElement : sourceElements){
                    assert sourceElement.getName().equals("source");
                    pattern.addDataSource(sourceElement.getText());
                }
                //matcher (optional)
                if(labelElements.size() == 4){
                    assert labelElements.get(3).getName().equals("matcher");
                    List<Element> matcherElements = labelElements.get(3).elements();
                    assert matcherElements.get(0).getName().equals("type");
                    String matcherType = matcherElements.get(0).getText();
                    if(matcherType.equals("primaryKey")){
                        assert matcherElements.get(1).getName().equals("primaryKey");
                        PrimaryKeyMatcher primaryKeyMatcher = new PrimaryKeyMatcher(matcherElements.get(1).getText());
                        assert matcherElements.get(2).getName().equals("optionalValueList");
                        List<Element> optionalValueElements = matcherElements.get(2).elements();
                        for(Element optionalValueElement : optionalValueElements){
                            assert optionalValueElement.getName().equals("value");
                            primaryKeyMatcher.addOptionalValue(optionalValueElement.getText());
                        }
                        pattern.setMatcher(primaryKeyMatcher);
                    }
                    else if(matcherType.equals("function")){
                        assert matcherElements.get(1).getName().equals("functionName");
                        FunctionMatcher functionMatcher = new FunctionMatcher(matcherElements.get(1).getText(), mfuncInstance);
                        //extraArgumentList (optional)
                        if(matcherElements.size() == 3){
                            assert matcherElements.get(2).getName().equals("extraArgumentList");
                            List<Element> extraArgElements = matcherElements.get(2).elements();
                            for(Element extraArgElement : extraArgElements){
                                assert extraArgElement.getName().equals("argument");
                                functionMatcher.addExtraArg(extraArgElement.getText());
                            }
                        }
                        pattern.setMatcher(functionMatcher);
                    }
                    else{
                        assert false;
                    }
                }
                patternHashMap.put(pattern.getPatternId(), pattern);
            }
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
        //init activateMaps
        initActivateContextsNumberMap(patternHashMap);
        return patternHashMap;
    }

    private Object loadMfuncFile(String mfuncFile) {
        Object mfuncInstance;
        Path mfuncPath = Paths.get(mfuncFile).toAbsolutePath();
        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{mfuncPath.getParent().toFile().toURI().toURL()})) {
            Class<?> clazz = classLoader.loadClass(mfuncPath.getFileName().toString().split("\\.")[0]);
            Constructor<?> constructor = clazz.getConstructor();
            mfuncInstance = constructor.newInstance();
        } catch (IOException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                 InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return mfuncInstance;
    }

    private void initActivateContextsNumberMap(HashMap<String, Pattern> patternHashMap){
        for(Pattern pattern : patternHashMap.values()){
            if(pattern.getFreshnessType() == FreshnessType.number){
                activateContextsNumberMap.put(pattern.getPatternId(), new LinkedBlockingQueue<>());
            }
        }
    }

    public synchronized void generateChanges(Message message){
        List<ContextChange> changeList = new ArrayList<>();
        //根据当前时间清理过时的contexts，生成相应的changes
        cleanOverdueContexts(changeList);

        if(message != null){
            //为message中的每一个context寻找对应的patterns，并生成相应的changes
            for(String contextId : message.getContextMap().keySet()){
                String fromSensorName = contextId.substring(0, contextId.lastIndexOf("_"));
                for(Pattern pattern : server.getPatternMap().values()){
                    if(pattern.getDataSourceType() == DataSourceType.pattern){
                        continue;
                    }
                    if(pattern.getDataSourceSet().contains(fromSensorName)){
                        Context context = message.getContextMap().get(contextId);
                        if(match(pattern, context)){
                            changeList.addAll(generate(pattern, context));
                        }
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
                throw new RuntimeException(e);
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
}
