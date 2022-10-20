package platform.service.ctx.ctxChecker.middleware.checkers;

import platform.service.ctx.rule.Rule;
import platform.service.ctx.ctxChecker.constraint.formulas.FForall;

import platform.service.ctx.ctxChecker.constraint.formulas.Formula;
import platform.service.ctx.ctxChecker.constraint.runtime.Link;
import platform.service.ctx.ctxChecker.constraint.runtime.RuntimeNode;
import platform.service.ctx.ctxChecker.context.Context;
import platform.service.ctx.ctxChecker.context.ContextPool;
import platform.service.ctx.ctxChecker.constraint.formulas.FExists;
import platform.service.ctx.ctxChecker.context.ContextChange;
import platform.service.ctx.ctxChecker.middleware.NotSupportedException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConC extends Checker {

    public final ExecutorService ThreadPool;

    public ConC(Map<String,Rule> ruleMap, ContextPool contextPool, Object bfunctions) {
        super(ruleMap, contextPool, bfunctions);
        ThreadPool = Executors.newFixedThreadPool(13);
        this.technique = "ConC";
    }

    public static class CreateBranchesTask_ConC implements Callable<RuntimeNode> {
        String rule_id;
        int depth;
        HashMap<String, Context> varEnv;
        Context context;
        Formula originFormula;
        Checker checker;

        public CreateBranchesTask_ConC(String rule_id, int depth,
                                       HashMap<String,Context> varEnv,
                                       Context context, Formula originFormula, Checker checker){
            this.rule_id = rule_id;
            this.depth = depth;
            this.varEnv = varEnv;
            this.context = context;
            this.originFormula = originFormula;
            this.checker = checker;
        }

        @Override
        public RuntimeNode call() {
            RuntimeNode returnNode;
            if(originFormula.getFormula_type() == Formula.Formula_Type.EXISTS){
                returnNode = new RuntimeNode(((FExists)originFormula).getSubformula());
                returnNode.setDepth(this.depth + 1);
                returnNode.getVarEnv().putAll(this.varEnv);
                returnNode.getVarEnv().put(((FExists)originFormula).getVar(), context);
                returnNode.getFormula().CreateBranches_ConC(rule_id, returnNode, ((FExists)originFormula).getSubformula(), false, checker);
            }
            else{
                returnNode = new RuntimeNode(((FForall)originFormula).getSubformula());
                returnNode.setDepth(this.depth + 1);
                returnNode.getVarEnv().putAll(this.varEnv);
                returnNode.getVarEnv().put(((FForall)originFormula).getVar(), context);
                returnNode.getFormula().CreateBranches_ConC(rule_id, returnNode, ((FForall)originFormula).getSubformula(), false, checker);
            }
            return returnNode;
        }
    }

    public static class TruthEvaluationTask_ConC implements Callable<Boolean>{
        RuntimeNode curNode;
        Formula originFormula;
        Checker checker;

        public TruthEvaluationTask_ConC(RuntimeNode curNode, Formula originFormula, Checker checker){
            this.curNode = curNode;
            this.originFormula = originFormula;
            this.checker = checker;
        }

        @Override
        public Boolean call() {
            return curNode.getFormula().TruthEvaluation_ConC(curNode, originFormula, false, checker);
        }
    }

    public static class LinksGenerationTask_ConC implements Callable<Set<Link>>{
        RuntimeNode curNode;
        Formula originFormula;
        Checker checker;

        public LinksGenerationTask_ConC(RuntimeNode curNode, Formula originFormula, Checker checker){
            this.curNode = curNode;
            this.originFormula = originFormula;
            this.checker = checker;
        }

        @Override
        public Set<Link> call(){
            return curNode.getFormula().LinksGeneration_ConC(curNode, originFormula, false, checker);
        }
    }

    @Override
    public void ctxChangeCheckIMD(ContextChange contextChange) {
        //consistency checking
        for(Rule rule : ruleMap.values()){
            if(rule.getVarPatternMap().containsValue(contextChange.getPatternId())){
                //apply changes
                contextPool.ApplyChange(rule.getRule_id(), contextChange);
                //build CCT
                rule.BuildCCT_CONC(this);
                //Truth value evaluation
                rule.TruthEvaluation_ConC(this);
                //Links Generation
                Set<Link> links = rule.LinksGeneration_ConC(this);
                if(links != null){
                    rule.addCriticalSet(links);
                }
                if(links != null){
                    storeLink(rule.getRule_id(), rule.getCCTRoot().isTruth(), links);
                }
            }
        }
    }

    @Override
    public void ctxChangeCheckBatch(Rule rule, List<ContextChange> batch) throws NotSupportedException {

        for(ContextChange contextChange : batch){
            contextPool.ApplyChange(rule.getRule_id(), contextChange);
        }
        rule.BuildCCT_CONC(this);
        rule.TruthEvaluation_ConC(this);
        Set<Link> links = rule.LinksGeneration_ConC(this);
        if(links != null){
            rule.addCriticalSet(links);
        }
        if(links != null){
            storeLink(rule.getRule_id(), rule.getCCTRoot().isTruth(), links);
        }
    }
}
