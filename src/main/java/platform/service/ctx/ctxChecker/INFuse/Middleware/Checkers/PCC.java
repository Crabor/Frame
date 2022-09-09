package platform.service.ctx.ctxChecker.INFuse.Middleware.Checkers;

import platform.service.ctx.ctxChecker.INFuse.Constraints.Rule;
import platform.service.ctx.ctxChecker.INFuse.Constraints.RuleHandler;
import platform.service.ctx.ctxChecker.INFuse.Constraints.Runtime.Link;
import platform.service.ctx.ctxChecker.INFuse.Contexts.ContextChange;
import platform.service.ctx.ctxChecker.INFuse.Contexts.ContextPool;

import java.util.List;
import java.util.Set;

public class PCC extends Checker{

    public PCC(RuleHandler ruleHandler, ContextPool contextPool, Object bfunctions) {
        super(ruleHandler, contextPool, bfunctions);
        this.technique = "PCC";
    }

    @Override
    public void ctxChangeCheckIMD(ContextChange contextChange) {
        //consistency checking
        for(Rule rule : ruleHandler.getRuleList()){
            if(rule.getRelatedPatterns().contains(contextChange.getPattern_id())){
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
        for(String pattern_id : rule.getRelatedPatterns()){
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
