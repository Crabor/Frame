package platform.service.ctx.ctxChecker.middleware.checkers;


import platform.service.ctx.rule.Rule;
import platform.service.ctx.ctxChecker.constraint.runtime.Link;
import platform.service.ctx.ctxChecker.context.ContextChange;
import platform.service.ctx.ctxChecker.context.ContextPool;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ECC extends Checker{

    public ECC(Map<String, Rule> ruleMap, ContextPool contextPool, Object bfunctions) {
        super(ruleMap, contextPool, bfunctions);
        this.technique = "ECC";
    }

    @Override
    public void ctxChangeCheckIMD(ContextChange contextChange) {
        //consistency checking
        for(Rule rule : this.ruleMap.values()){
            if (rule.getVarPatternMap().containsValue(contextChange.getPatternId())){
                //apply change
                contextPool.ApplyChange(rule.getRule_id(), contextChange);
                //build CCT
                rule.BuildCCT_ECCPCC(this);
                //truth value evaluation
                rule.TruthEvaluation_ECC(this);
                //links generation
                Set<Link> links = rule.LinksGeneration_ECC(this);
                if(links != null){
                    storeLink(rule.getRule_id(), rule.getCCTRoot().isTruth(), links);
                }
            }
        }
    }

    @Override
    public void ctxChangeCheckBatch(Rule rule, List<ContextChange> batch){
        //apply change
        for(ContextChange contextChange : batch){
            contextPool.ApplyChange(rule.getRule_id(), contextChange);
        }
        //build CCT
        rule.BuildCCT_ECCPCC(this);
        //truth value evaluation
        rule.TruthEvaluation_ECC(this);
        //links generation
        Set<Link> links = rule.LinksGeneration_ECC(this);
        if(links != null){
            rule.addCriticalSet(links);
        }
        if(links != null){
            storeLink(rule.getRule_id(), rule.getCCTRoot().isTruth(), links);
        }
    }
}
