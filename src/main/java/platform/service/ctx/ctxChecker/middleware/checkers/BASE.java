package platform.service.ctx.ctxChecker.middleware.checkers;

import platform.service.ctx.rule.Rule;
import platform.service.ctx.ctxChecker.constraint.runtime.Link;
import platform.service.ctx.ctxChecker.context.ContextChange;
import platform.service.ctx.ctxChecker.context.ContextPool;
import platform.service.ctx.ctxChecker.middleware.NotSupportedException;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class BASE extends ConC{

    public BASE(Map<String, Rule> ruleMap, ContextPool contextPool, Object bfunctions) {
        super(ruleMap, contextPool, bfunctions);
        this.technique = "BASE";
    }

    @Override
    public void ctxChangeCheckIMD(ContextChange contextChange) {
        for(Rule rule : ruleMap.values()){
            if(rule.getRelatedPatterns().contains(contextChange.getPatternId())){
                //apply changes
                contextPool.ApplyChange(rule.getRule_id(), contextChange);
                rule.UpdateAffectedWithOneChange(contextChange, this);
                //modify CCT
                rule.ModifyCCT_BASE(contextChange, this);
                //truth evaluation
                rule.TruthEvaluation_BASE(contextChange, this);
                //links generation
                Set<Link> links = rule.LinksGeneration_BASE(contextChange, this);
                if(links != null){
                    rule.addCriticalSet(links);
                }
                rule.CleanAffected();
                if(links != null){
                    storeLink(rule.getRule_id(), rule.getCCTRoot().isTruth(), links);
                }
                /*
                //modify CCT
                if(rule.isCCTAlready()){
                    rule.ModifyCCT_BASE(contextChange, this);
                    //truth evaluation
                    rule.TruthEvaluation_BASE(contextChange, this);
                    //links generation
                    Set<Link> links = rule.LinksGeneration_BASE(contextChange, this);
                    if(links != null){
                        rule.addCriticalSet(links);
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
                    }
                    rule.CleanAffected();
                    if(links != null){
                        storeLink(rule.getRule_id(), rule.getCCTRoot().isTruth(), links);
                    }
                }

                 */
            }
        }
    }

    @Override
    public void ctxChangeCheckBatch(Rule rule, List<ContextChange> batch) throws NotSupportedException {
        throw new NotSupportedException("not support");
    }

}
