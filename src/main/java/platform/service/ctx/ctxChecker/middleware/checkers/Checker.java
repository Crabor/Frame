package platform.service.ctx.ctxChecker.middleware.checkers;


import platform.service.ctx.rule.Rule;
import platform.service.ctx.ctxChecker.constraint.runtime.Link;
import platform.service.ctx.ctxChecker.context.ContextPool;
import platform.service.ctx.ctxChecker.context.ContextChange;
import platform.service.ctx.ctxChecker.middleware.NotSupportedException;

import java.util.*;

public abstract class Checker {
    protected final Map<String, Rule> ruleMap;

    protected ContextPool contextPool;
    protected String technique;
    protected Object bfuncInstance;

    // rule_id -> [(truthValue1, linkSet1), (truthValue2,linkSet2)]
    protected Map<String, List<Map.Entry<Boolean, Set<Link>>>> ruleLinksMap;

    protected Map<String, Set<Link>> tempRuleLinksMap;


    public Checker(Map<String, Rule> ruleMap, ContextPool contextPool, Object bfuncInstance) {
        this.ruleMap = ruleMap;
        this.contextPool = contextPool;
        this.bfuncInstance = bfuncInstance;
        this.ruleLinksMap = new HashMap<>();
        this.tempRuleLinksMap = new HashMap<>();
    }

    protected void storeLink(String rule_id, boolean truth, Set<Link> linkSet){
        this.ruleLinksMap.computeIfAbsent(rule_id, k -> new ArrayList<>());
        Objects.requireNonNull(this.ruleLinksMap.computeIfPresent(rule_id, (k, v) -> v)).add(
                new AbstractMap.SimpleEntry<>(truth, linkSet)
        );

        if(!linkSet.isEmpty()){
            this.tempRuleLinksMap.computeIfAbsent(rule_id, k -> new HashSet<>(linkSet));
            Objects.requireNonNull(this.tempRuleLinksMap.computeIfPresent(rule_id, (k, v) -> v)).addAll(linkSet);
        }
    }

    public void checkInit(){
        for(Rule rule : ruleMap.values()){
            rule.BuildCCT_ECCPCC(this);
            rule.TruthEvaluation_ECC(this);
            rule.LinksGeneration_ECC(this);
        }
    }
    public abstract void ctxChangeCheckIMD(ContextChange contextChange);
    public abstract void ctxChangeCheckBatch(Rule rule, List<ContextChange> batch) throws NotSupportedException;


    //getter
    public ContextPool getContextPool() {
        return contextPool;
    }
    public String getTechnique() {
        return technique;
    }

    public Object getBfuncInstance() {
        return bfuncInstance;
    }

    public Map<String, List<Map.Entry<Boolean, Set<Link>>>> getRuleLinksMap() {
        return ruleLinksMap;
    }

    public Map<String, Set<Link>> getTempRuleLinksMap() {
        return tempRuleLinksMap;
    }
}
