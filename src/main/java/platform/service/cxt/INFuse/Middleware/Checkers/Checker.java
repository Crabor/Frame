package platform.service.cxt.INFuse.Middleware.Checkers;

import platform.service.cxt.INFuse.Constraints.Rule;
import platform.service.cxt.INFuse.Constraints.RuleHandler;
import platform.service.cxt.INFuse.Constraints.Runtime.Link;
import platform.service.cxt.INFuse.Contexts.ContextChange;
import platform.service.cxt.INFuse.Contexts.ContextPool;
import platform.service.cxt.INFuse.Middleware.NotSupportedException;

import java.util.*;

public abstract class Checker {
    protected RuleHandler ruleHandler;
    protected ContextPool contextPool;
    protected String technique;
    protected Object bfuncInstance;

    // rule_id -> [(truthValue1, linkSet1), (truthValue2,linkSet2)]
    protected Map<String, List<Map.Entry<Boolean, Set<Link>>>> ruleLinksMap;

    protected Map<String, Set<Link>> tempRuleLinksMap;


    public Checker(RuleHandler ruleHandler, ContextPool contextPool, Object bfuncInstance) {
        this.ruleHandler = ruleHandler;
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

        this.tempRuleLinksMap.computeIfAbsent(rule_id, k -> new HashSet<>(linkSet));
        Objects.requireNonNull(this.tempRuleLinksMap.computeIfPresent(rule_id, (k, v) -> v)).addAll(linkSet);
    }

    public void checkInit(){
        for(Rule rule : ruleHandler.getRuleList()){
            rule.BuildCCT_ECCPCC(this);
            rule.TruthEvaluation_ECC(this);
            rule.LinksGeneration_ECC(this);
        }
    }
    public abstract void ctxChangeCheckIMD(ContextChange contextChange);
    public abstract void ctxChangeCheckBatch(Rule rule, List<ContextChange> batch) throws NotSupportedException;


    //getter
    public RuleHandler getRuleHandler() {
        return ruleHandler;
    }

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
