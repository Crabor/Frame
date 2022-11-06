package platform.service.ctx.ctxServer;

import com.alibaba.fastjson.JSONObject;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import platform.config.Configuration;
import platform.comm.pubsub.AbstractSubscriber;
import platform.service.ctx.ctxChecker.context.Context;
import platform.service.ctx.message.Message;
import platform.service.ctx.pattern.matcher.FunctionMatcher;
import platform.service.ctx.pattern.matcher.PrimaryKeyMatcher;
import platform.service.ctx.pattern.Pattern;
import platform.service.ctx.pattern.types.DataSourceType;
import platform.service.ctx.pattern.types.FreshnessType;
import platform.service.ctx.rule.resolver.Resolver;
import platform.service.ctx.rule.resolver.ResolverType;
import platform.service.ctx.rule.Rule;
import platform.service.ctx.ctxChecker.constraint.formulas.*;
import platform.service.ctx.ctxChecker.constraint.runtime.RuntimeNode;
import platform.service.ctx.ctxChecker.context.ContextChange;
import platform.service.ctx.statistics.ServerStatistics;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;


public abstract class AbstractCtxServer extends AbstractSubscriber implements Runnable{
    protected Thread t;
    protected HashMap<String, Pattern> patternMap;
    protected HashMap<String, Rule> ruleMap;
    protected HashMap<String, Resolver> resolverMap;
    protected final Map<Long, Message> originalMsgSet = new ConcurrentHashMap<>();
    protected final Map<Long, Message> fixingMsgSet = new TreeMap<>(Long::compareTo);

    protected ChgGenerator chgGenerator;
    protected final LinkedBlockingQueue<ContextChange> changeBuffer = new LinkedBlockingQueue<>();

    protected CtxFixer ctxFixer;

    protected ServerStatistics serverStatistics;

    public abstract void init();

    //pattern related
    public void buildPatterns(String patternFile, String mfuncFile){
        if(patternFile == null || patternFile.equals("")){
            return;
        }

        Object mfuncInstance = loadMfuncFile(mfuncFile);
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
                patternMap.put(pattern.getPatternId(), pattern);
            }
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    private Object loadMfuncFile(String mfuncFile) {
        if(mfuncFile == null || mfuncFile.equals("")){
            return null;
        }
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

    public HashMap<String, Pattern> getPatternMap() {
        return patternMap;
    }

    //rule related
    public void buildRules(String ruleFile){
        if(ruleFile == null || ruleFile.equals("")){
            return;
        }

        try {
            SAXReader saxReader = new SAXReader();
            Document document = null;
            document = saxReader.read(new File(ruleFile));
            List<Element> eRuleList = document.getRootElement().elements();
            for(Element eRule: eRuleList){
                List<Element> eLabelList = eRule.elements();
                assert eLabelList.size() == 3;
                //id
                assert eLabelList.get(0).getName().equals("id");
                Rule newRule = new Rule(eLabelList.get(0).getText());
                // formula
                assert eLabelList.get(1).getName().equals("formula");
                Element eFormula =  eLabelList.get(1).elements().get(0);
                newRule.setFormula(resolveFormula(eFormula, newRule.getVarPatternMap(), newRule.getPatToFormula(), newRule.getPatToRuntimeNode(), 0));
                setPatWithDepth(newRule.getFormula(), newRule.getPatToDepth(), newRule.getDepthToPat());
                ruleMap.put(newRule.getRule_id(), newRule);
                // resolver
                resolverMap.put(newRule.getRule_id(), buildResolver(eLabelList.get(2).elements()));
            }
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    private Formula resolveFormula(Element eFormula, Map<String, String> varPatternMap, Map<String, Formula> patToFormula,
                                   Map<String, Set<RuntimeNode>> patToRunTimeNode, int depth){
        Formula retformula = null;
        switch (eFormula.getName()){
            case "forall":{
                FForall tempforall = new FForall(eFormula.attributeValue("var"), eFormula.attributeValue("in"));
                // forall has only one kid
                tempforall.setSubformula(resolveFormula(eFormula.elements().get(0), varPatternMap, patToFormula, patToRunTimeNode, depth + 1));
                varPatternMap.put(eFormula.attributeValue("var"), eFormula.attributeValue("in"));
                patToFormula.put(eFormula.attributeValue("in"), tempforall);
                patToRunTimeNode.put(eFormula.attributeValue("in"), new HashSet<>());
                retformula = tempforall;
                break;
            }
            case "exists":{
                FExists tempexists = new FExists(eFormula.attributeValue("var"), eFormula.attributeValue("in"));
                // exists has only one kid
                tempexists.setSubformula(resolveFormula(eFormula.elements().get(0), varPatternMap, patToFormula, patToRunTimeNode, depth + 1));
                varPatternMap.put(eFormula.attributeValue("var"), eFormula.attributeValue("in"));
                patToFormula.put(eFormula.attributeValue("in"), tempexists);
                patToRunTimeNode.put(eFormula.attributeValue("in"), new HashSet<>());
                retformula = tempexists;
                break;
            }
            case "and":{
                FAnd tempand = new FAnd();
                // and has two kids
                tempand.replaceSubformula(0, resolveFormula(eFormula.elements().get(0), varPatternMap, patToFormula, patToRunTimeNode, depth + 1));
                tempand.replaceSubformula(1, resolveFormula(eFormula.elements().get(1), varPatternMap, patToFormula, patToRunTimeNode, depth + 1));
                retformula = tempand;
                break;
            }
            case "or" :{
                FOr tempor = new FOr();
                // or has two kids
                tempor.replaceSubformula(0, resolveFormula(eFormula.elements().get(0), varPatternMap, patToFormula, patToRunTimeNode, depth + 1));
                tempor.replaceSubformula(1, resolveFormula(eFormula.elements().get(1), varPatternMap, patToFormula, patToRunTimeNode, depth + 1));
                retformula = tempor;
                break;
            }
            case "implies" :{
                FImplies tempimplies = new FImplies();
                // implies has two kids
                tempimplies.replaceSubformula(0, resolveFormula(eFormula.elements().get(0), varPatternMap, patToFormula, patToRunTimeNode, depth + 1));
                tempimplies.replaceSubformula(1, resolveFormula(eFormula.elements().get(1), varPatternMap, patToFormula, patToRunTimeNode, depth + 1));
                retformula = tempimplies;
                break;
            }
            case "not" :{
                FNot tempnot = new FNot();
                // not has only one kid
                tempnot.setSubformula(resolveFormula(eFormula.elements().get(0), varPatternMap, patToFormula, patToRunTimeNode, depth + 1));
                retformula = tempnot;
                break;
            }
            case "bfunction" :{
                FBfunc tempbfunc = new FBfunc(eFormula.attributeValue("name"));
                // bfunc has several params
                List<Element> Eparamlist = eFormula.elements();
                for(Element Eparam : Eparamlist){
                    tempbfunc.addParam(Eparam.attributeValue("pos"), Eparam.attributeValue("var"));
                }
                retformula = tempbfunc;
                break;
            }
            default:
                assert false;
        }

        return retformula;
    }

    private int setPatWithDepth(Formula formula, Map<String,Integer> patToDepth, Map<Integer, String> depthToPat){
        int maxDepth;
        switch (formula.getFormula_type()){
            case FORALL:
                maxDepth = setPatWithDepth(((FForall)formula).getSubformula(), patToDepth, depthToPat);
                patToDepth.put(((FForall)formula).getPattern_id(), maxDepth);
                depthToPat.put(maxDepth, ((FForall)formula).getPattern_id());
                return maxDepth + 1;
            case EXISTS:
                maxDepth = setPatWithDepth(((FExists)formula).getSubformula(), patToDepth, depthToPat);
                patToDepth.put(((FExists)formula).getPattern_id(), maxDepth);
                depthToPat.put(maxDepth, ((FExists)formula).getPattern_id());
                return maxDepth + 1;
            case AND:
                maxDepth = setPatWithDepth(((FAnd)formula).getSubformulas()[0], patToDepth, depthToPat);
                maxDepth = Math.max(maxDepth, setPatWithDepth(((FAnd)formula).getSubformulas()[1], patToDepth, depthToPat));
                return maxDepth + 1;
            case OR:
                maxDepth = setPatWithDepth(((FOr)formula).getSubformulas()[0], patToDepth, depthToPat);
                maxDepth = Math.max(maxDepth, setPatWithDepth(((FOr)formula).getSubformulas()[1], patToDepth, depthToPat));
                return maxDepth + 1;
            case IMPLIES:
                maxDepth = setPatWithDepth(((FImplies)formula).getSubformulas()[0], patToDepth, depthToPat);
                maxDepth = Math.max(maxDepth, setPatWithDepth(((FImplies)formula).getSubformulas()[1], patToDepth, depthToPat));
                return maxDepth + 1;
            case NOT:
                maxDepth = setPatWithDepth(((FNot)formula).getSubformula(), patToDepth, depthToPat);
                return maxDepth + 1;
            case BFUNC:
                return 1;
            default:
                return -1;
        }
    }

    private Resolver buildResolver(List<Element> resolverElements){
        Resolver resolver = new Resolver();
        //type
        assert resolverElements.get(0).getName().equals("type");
        resolver.setResolverType(ResolverType.valueOf(resolverElements.get(0).getText()));
        //variable
        assert resolverElements.get(1).getName().equals("variable");
        resolver.setVariable(resolverElements.get(1).getText());
        //fixingPairList
        if(resolverElements.size() == 3){
            assert resolverElements.get(2).getName().equals("fixingPairList");
            List<Element> fixingPairElements = resolverElements.get(2).elements();
            for(Element fixingPairElement : fixingPairElements){
                assert fixingPairElement.getName().equals("fixingPair");
                assert fixingPairElement.elements().get(0).getName().equals("field");
                assert fixingPairElement.elements().get(1).getName().equals("value");
                resolver.addFixingPair(fixingPairElement.elements().get(0).getText(), fixingPairElement.elements().get(1).getText());
            }
        }
        return resolver;
    }

    public HashMap<String, Rule> getRuleMap() {
        return ruleMap;
    }

    public HashMap<String, Resolver> getResolverMap() {
        return resolverMap;
    }

    //message related
    protected void filterMessage(JSONObject msgJsonObj){
        Set<String> registeredSensorSet = Configuration.getRegisteredSensors();
        Iterator<String> iterator = msgJsonObj.keySet().iterator();
        while(iterator.hasNext()){
            String msgSensor = iterator.next();
            if(msgSensor.equals("index"))
                continue;
            if(!registeredSensorSet.contains(msgSensor)){
                iterator.remove();
            }
        }
    }

    protected void addOriginalMsg(Message message){
        this.originalMsgSet.put(message.getIndex(), message);
    }

    protected Message getOriginalMsg(long index){
        return this.originalMsgSet.get(index);
    }

    protected Message getOrPutDefaultFixingMsg(long index){
        Message message = this.fixingMsgSet.getOrDefault(index, new Message(index));
        this.fixingMsgSet.put(index, message);
        return message;
    }

    protected void removeOriginalMsg(long index){
        this.originalMsgSet.remove(index);
    }

    protected void removeFixingMsg(long index){
        this.fixingMsgSet.remove(index);
    }

    protected abstract void publishAndClean(long indexLimit);

    //chgGenerator related
    public void changeBufferProducer(List<ContextChange> changeList){
        changeBuffer.addAll(changeList);
    }

    public List<ContextChange> changeBufferConsumer(){
        List<ContextChange> changeList = new ArrayList<>();
        ContextChange change = null;
        try {
            change = changeBuffer.take();
            changeList.add(change);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        while(!changeBuffer.isEmpty()){
            change = changeBuffer.poll();
            changeList.add(change);
        }
        return changeList;
    }

    //fixer related
    public CtxFixer getCtxFixer() {
        return ctxFixer;
    }

    public ServerStatistics getServerStatistics() {
        return serverStatistics;
    }

    //run
    @Override
    public void run() {
        while(true){
            try {
                Map.Entry<String, Context> fixedContext = ctxFixer.getFixedContextQue().take();
                long index = Long.parseLong(fixedContext.getKey().substring(fixedContext.getKey().lastIndexOf("_") + 1));
                Message fixingMsg = getOrPutDefaultFixingMsg(index);
                fixingMsg.addContext(fixedContext.getKey(), fixedContext.getValue());
                publishAndClean(index);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void start() {
        if (t == null) {
            t = new Thread(this, getClass().getName());
            t.start();
        }
    }
}
