package platform.service.ctx.rule;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import platform.service.ctx.ctxChecker.constraint.formulas.*;
import platform.service.ctx.ctxChecker.constraint.runtime.RuntimeNode;
import platform.service.ctx.ctxServer.AbstractCtxServer;
import platform.service.ctx.rule.resolver.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class RuleManager {

    private final AbstractCtxServer ctxServer;
    private final HashMap<String, Rule> ruleMap;
    private ResolverType resolverType;

    public RuleManager(AbstractCtxServer ctxServer, ResolverType resolverType) {
        this.ctxServer = ctxServer;
        this.ruleMap = new HashMap<>();
        this.resolverType = resolverType;
    }

    public void buildRules(String ruleFile, String rfuncFile){
        if(ruleFile == null || ruleFile.equals("")){
            return;
        }

        Object rfuncInstance = loadRfuncFile(rfuncFile);
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
                newRule.setFormula(buildFormula(eFormula, newRule.getVarPatternMap(), newRule.getPatToFormula(), newRule.getPatToRuntimeNode(), 0));
                setPatWithDepth(newRule.getFormula(), newRule.getPatToDepth(), newRule.getDepthToPat());
                ruleMap.put(newRule.getRule_id(), newRule);
                // resolver
                newRule.setAbstractResolver(buildResolver(eLabelList.get(2).elements(), rfuncInstance));
            }
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    private Object loadRfuncFile(String rfuncFile){
        if(rfuncFile == null || rfuncFile.equals("")){
            return null;
        }
        Object rfuncInstance;
        Path rfuncPath = Paths.get(rfuncFile).toAbsolutePath();
        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{rfuncPath.getParent().toFile().toURI().toURL()})) {
            Class<?> clazz = classLoader.loadClass(rfuncPath.getFileName().toString().split("\\.")[0]);
            Constructor<?> constructor = clazz.getConstructor();
            rfuncInstance = constructor.newInstance();
        } catch (IOException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                 InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return rfuncInstance;
    }

    private Formula buildFormula(Element eFormula, Map<String, String> varPatternMap, Map<String, Formula> patToFormula,
                                 Map<String, Set<RuntimeNode>> patToRunTimeNode, int depth){
        Formula retformula = null;
        switch (eFormula.getName()){
            case "forall":{
                FForall tempforall = new FForall(eFormula.attributeValue("var"), eFormula.attributeValue("in"));
                // forall has only one kid
                tempforall.setSubformula(buildFormula(eFormula.elements().get(0), varPatternMap, patToFormula, patToRunTimeNode, depth + 1));
                varPatternMap.put(eFormula.attributeValue("var"), eFormula.attributeValue("in"));
                patToFormula.put(eFormula.attributeValue("in"), tempforall);
                patToRunTimeNode.put(eFormula.attributeValue("in"), new HashSet<>());
                retformula = tempforall;
                break;
            }
            case "exists":{
                FExists tempexists = new FExists(eFormula.attributeValue("var"), eFormula.attributeValue("in"));
                // exists has only one kid
                tempexists.setSubformula(buildFormula(eFormula.elements().get(0), varPatternMap, patToFormula, patToRunTimeNode, depth + 1));
                varPatternMap.put(eFormula.attributeValue("var"), eFormula.attributeValue("in"));
                patToFormula.put(eFormula.attributeValue("in"), tempexists);
                patToRunTimeNode.put(eFormula.attributeValue("in"), new HashSet<>());
                retformula = tempexists;
                break;
            }
            case "and":{
                FAnd tempand = new FAnd();
                // and has two kids
                tempand.replaceSubformula(0, buildFormula(eFormula.elements().get(0), varPatternMap, patToFormula, patToRunTimeNode, depth + 1));
                tempand.replaceSubformula(1, buildFormula(eFormula.elements().get(1), varPatternMap, patToFormula, patToRunTimeNode, depth + 1));
                retformula = tempand;
                break;
            }
            case "or" :{
                FOr tempor = new FOr();
                // or has two kids
                tempor.replaceSubformula(0, buildFormula(eFormula.elements().get(0), varPatternMap, patToFormula, patToRunTimeNode, depth + 1));
                tempor.replaceSubformula(1, buildFormula(eFormula.elements().get(1), varPatternMap, patToFormula, patToRunTimeNode, depth + 1));
                retformula = tempor;
                break;
            }
            case "implies" :{
                FImplies tempimplies = new FImplies();
                // implies has two kids
                tempimplies.replaceSubformula(0, buildFormula(eFormula.elements().get(0), varPatternMap, patToFormula, patToRunTimeNode, depth + 1));
                tempimplies.replaceSubformula(1, buildFormula(eFormula.elements().get(1), varPatternMap, patToFormula, patToRunTimeNode, depth + 1));
                retformula = tempimplies;
                break;
            }
            case "not" :{
                FNot tempnot = new FNot();
                // not has only one kid
                tempnot.setSubformula(buildFormula(eFormula.elements().get(0), varPatternMap, patToFormula, patToRunTimeNode, depth + 1));
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

    private AbstractResolver buildResolver(List<Element> resolverElements, Object rfuncInstance){
        //strategy
        assert resolverElements.get(0).getName().equals("strategy");
        if(resolverElements.get(0).getText().equals("drop-latest")){
            DropLatestResolver dropLatestResolver = new DropLatestResolver();
            //group
            assert resolverElements.get(1).getName().equals("group");
            dropLatestResolver.addGroup(resolverElements.get(1).getText());
            //priority
            assert resolverElements.get(2).getName().equals("priority");
            dropLatestResolver.setPriority(Integer.parseInt(resolverElements.get(2).getText()));
            return dropLatestResolver;
        }
        else if(resolverElements.get(0).getText().equals("drop-all")){
            DropAllResolver dropAllResolver = new DropAllResolver();
            //group
            assert resolverElements.get(1).getName().equals("group");
            dropAllResolver.addGroup(resolverElements.get(1).getText());
            //priority
            assert resolverElements.get(2).getName().equals("priority");
            dropAllResolver.setPriority(Integer.parseInt(resolverElements.get(2).getText()));
            return dropAllResolver;
        }
        else{
            assert resolverElements.get(0).getText().equals("customized");
            CustomizedResolver customizedResolver = new CustomizedResolver(rfuncInstance);
            //group
            assert resolverElements.get(1).getName().equals("group");
            customizedResolver.addGroup(resolverElements.get(1).getText());
            //priority
            assert resolverElements.get(2).getName().equals("priority");
            customizedResolver.setPriority(Integer.parseInt(resolverElements.get(2).getText()));
            //functionName
            assert resolverElements.get(3).getName().equals("functionName");
            customizedResolver.setFuncName(resolverElements.get(3).getText());
            return customizedResolver;
        }
    }


    public Rule getRule(String ruleId){
        return ruleMap.get(ruleId);
    }

    public AbstractResolver getResolver(String ruleId){
        return ruleMap.get(ruleId).getAbstractResolver();
    }

    public AbstractCtxServer getCtxServer() {
        return ctxServer;
    }

    public HashMap<String, Rule> getRuleMap() {
        return ruleMap;
    }

    public ResolverType getResolverType() {
        return resolverType;
    }

    public void reset(String ruleFile, String rfuncFile, ResolverType resolverType){
        this.ruleMap.clear();
        this.resolverType = resolverType;
        buildRules(ruleFile, rfuncFile);
    }
}
