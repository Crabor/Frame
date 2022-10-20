package platform.service.ctx.ctxChecker.middleware.checkers;


import platform.service.ctx.rule.Rule;
import platform.service.ctx.ctxChecker.constraint.runtime.Link;
import platform.service.ctx.ctxChecker.context.ContextPool;
import platform.service.ctx.ctxChecker.context.ContextChange;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class PCC extends Checker{

    public PCC(Map<String, Rule> ruleMap, ContextPool contextPool, Object bfunctions) {
        super(ruleMap, contextPool, bfunctions);
        this.technique = "PCC";
    }

    @Override
    public void ctxChangeCheckIMD(ContextChange contextChange) {
        //consistency checking
        for(Rule rule : ruleMap.values()){
            if(rule.getVarPatternMap().containsValue(contextChange.getPatternId())){
                //apply changes
                contextPool.ApplyChange(rule.getRule_id(), contextChange);
                rule.UpdateAffectedWithOneChange(contextChange, this);
                /*
                //modify CCT
                if(rule.isCCTAlready()){
                    rule.ModifyCCT_PCC(contextChange, this);
                    //truth evaluation
                    rule.TruthEvaluation_PCC(contextChange, this);
                    //links generation
                    Set<Link> links = rule.LinksGeneration_PCC(contextChange, this);
                    if(links != null){
                        rule.addCriticalSet(links);
                        //rule.oracleCount(links, contextChange);
                    }
                    rule.CleanAffected();
                    if(links != null){
                        storeLink(rule.getRule_id(), rule.getCCTRoot().isTruth(), links);
                    }
                }
                //build CCT
                else{
                    //same as ECC
                    rule.BuildCCT_ECCPCC(this);
                    //truth evaluation
                    rule.TruthEvaluation_ECC(this);
                    //links generation
                    Set<Link> links = rule.LinksGeneration_ECC(this);
                    if(links != null){
                        rule.addCriticalSet(links);
                        //rule.oracleCount(links, contextChange);
                    }
                    rule.CleanAffected();
                    if(links != null){
                        storeLink(rule.getRule_id(), rule.getCCTRoot().isTruth(), links);
                    }
                }
                 */

                rule.ModifyCCT_PCC(contextChange, this);
                //truth evaluation
                rule.TruthEvaluation_PCC(contextChange, this);
                //links generation
                Set<Link> links = rule.LinksGeneration_PCC(contextChange, this);
                if(links != null){
                    rule.addCriticalSet(links);
                    //rule.oracleCount(links, contextChange);
                }
                rule.CleanAffected();
                if(links != null){
                    storeLink(rule.getRule_id(), rule.getCCTRoot().isTruth(), links);
                }
            }
        }
    }

    @Override
    public void ctxChangeCheckBatch(Rule rule, List<ContextChange> batch) {
        //rule.intoFile(batch);
        //clean
        for(String pattern_id : rule.getVarPatternMap().values()){
            contextPool.GetAddSet(pattern_id).clear();
            contextPool.GetDelSet(pattern_id).clear();
            contextPool.GetUpdSet(pattern_id).clear();
        }
        for(ContextChange contextChange : batch){
            contextPool.ApplyChangeWithSets(rule.getRule_id(), contextChange);
            /*
            if(rule.isCCTAlready()){
                rule.ModifyCCT_PCCM(contextChange, this);
            }
            else{
                rule.BuildCCT_ECCPCC(this);
            }

             */
            rule.ModifyCCT_PCCM(contextChange, this);
        }
        rule.UpdateAffectedWithChanges(this);
        rule.TruthEvaluation_PCCM(this);
        Set<Link> links = rule.LinksGeneration_PCCM(this);
        if(links != null){
            rule.addCriticalSet(links);
        }
        rule.CleanAffected();
        if(links != null){
            storeLink(rule.getRule_id(), rule.getCCTRoot().isTruth(), links);
        }
    }

}
