package platform.service.ctx.ctxChecker.middleware.schedulers;

import platform.service.ctx.rule.Rule;
import platform.service.ctx.ctxChecker.constraint.runtime.RuntimeNode;
import platform.service.ctx.ctxChecker.context.Context;
import platform.service.ctx.ctxChecker.constraint.formulas.FExists;
import platform.service.ctx.ctxChecker.constraint.formulas.FForall;
import platform.service.ctx.ctxChecker.constraint.formulas.Formula;
import platform.service.ctx.ctxChecker.context.ContextChange;
import platform.service.ctx.ctxChecker.context.ContextPool;
import platform.service.ctx.ctxChecker.middleware.checkers.Checker;
import platform.service.ctx.ctxChecker.middleware.checkers.ConC;
import platform.service.ctx.ctxChecker.middleware.checkers.INFUSE_C;
import platform.service.ctx.ctxChecker.middleware.NotSupportedException;

import java.util.*;

public class INFUSE_S extends Scheduler{
    private final Map<String, Rule> ruleMap;

    public INFUSE_S(Map<String, Rule> ruleMap, ContextPool contextPool, Checker checker) {
        super(contextPool, checker);
        this.ruleMap = ruleMap;
        this.strategy = "INFUSE_S";
    }

    @Override
    public void doSchedule(ContextChange contextChange) throws Exception {
        Batch_Form_DIS(contextChange);
        for(Rule rule : ruleMap.values()){
            if(rule.getNewBatch() != null){
                this.checker.ctxChangeCheckBatch(rule, rule.getBatch());
                rule.setBatch(rule.getNewBatch());
                rule.setNewBatch(null);
                this.riskRefresh(rule);
            }
        }
    }

    private void Batch_Form_DIS(ContextChange newChange){
        for(Rule rule : ruleMap.values()){
            if(!rule.getVarPatternMap().containsValue(newChange.getPatternId()))
                continue;

            if(riskMatch(rule, newChange)){
                List<ContextChange> newBatch = new ArrayList<>();
                newBatch.add(newChange);
                rule.setNewBatch(newBatch);
            }
            else{
                if(rule.getBatch() != null){
                    rule.getBatch().add(newChange);
                }
                else{
                    List<ContextChange> batch = new ArrayList<>();
                    batch.add(newChange);
                    rule.setBatch(batch);
                }
            }
        }
    }

    private boolean riskMatch(Rule rule, ContextChange newChange){
        String pattern_id = newChange.getPatternId();
        Formula formula = rule.getPatToFormula().get(pattern_id);
        Map<ContextChange.ChangeType, Set<RuntimeNode.Virtual_Truth_Type>> rcSet = null;
        Map<ContextChange.ChangeType, Set<RuntimeNode.Virtual_Truth_Type>> reSet = null;
        if(formula.getFormula_type() == Formula.Formula_Type.FORALL){
            rcSet = ((FForall) formula).getRcSet();
            reSet = ((FForall) formula).getReSet();
        }
        else{
            rcSet = ((FExists) formula).getRcSet();
            reSet = ((FExists) formula).getReSet();
        }
        assert rcSet != null && reSet != null;
        //匹配
        if(rule.isRiskAlready()){
            ContextChange.ChangeType reType = reSet.containsKey(ContextChange.ChangeType.ADDITION) ? ContextChange.ChangeType.ADDITION : ContextChange.ChangeType.DELETION;
            Set<RuntimeNode> runtimeNodeSet = rule.getPatToRuntimeNode().get(pattern_id);
            boolean reFlag = false;
            if(newChange.getChangeType() == ContextChange.ChangeType.ADDITION){
                if(newChange.getChangeType() == reType){
                    reFlag = true;
                }
                if(!reFlag){
                    long oldTime = System.nanoTime();
                    for(RuntimeNode runtimeNode : runtimeNodeSet){
                        runtimeNode.vtPropagationAdd(newChange.getContext());
                        //runtimeNode.virtualTruthUpdating(ContextChange.Change_Type.ADD, RuntimeNode.Virtual_Truth_Type.UNKNOWN, null);
                    }
                }
            }
            else{
                if(newChange.getChangeType() == reType){
                    for(RuntimeNode runtimeNode : runtimeNodeSet){
                        HashMap<Context, RuntimeNode.Virtual_Truth_Type> kidsVT = runtimeNode.getKidsVT();
                        RuntimeNode.Virtual_Truth_Type kidVT = kidsVT.get(newChange.getContext());
                        if(kidVT == RuntimeNode.Virtual_Truth_Type.UNKNOWN){
                            reFlag = true;
                            break;
                        }
                    }
                    if(!reFlag){
                        //检查batch
                        for(ContextChange change : rule.getBatch()){
                            if(change.getChangeType() == ContextChange.ChangeType.ADDITION &&
                                change.getPatternId().equals(pattern_id) &&
                                change.getContext().equals(newChange.getContext())){
                                reFlag = true;
                                break;
                            }
                        }
                    }
                }
                if(!reFlag){
                    for(RuntimeNode runtimeNode : runtimeNodeSet){
                        HashMap<Context, RuntimeNode.Virtual_Truth_Type> kidsVT = runtimeNode.getKidsVT();
                        RuntimeNode.Virtual_Truth_Type kidVT = kidsVT.get(newChange.getContext());
                        if(kidVT != null){
                            runtimeNode.vtPropagationDelete(kidVT, newChange.getContext());
                        }
                    }
                }
            }
            return reFlag;
        }
        else{
            ContextChange.ChangeType rcType = rcSet.containsKey(ContextChange.ChangeType.ADDITION) ? ContextChange.ChangeType.ADDITION : ContextChange.ChangeType.DELETION;
            Set<RuntimeNode> runtimeNodeSet = rule.getPatToRuntimeNode().get(pattern_id);
            boolean rcFlag = false;
            if(newChange.getChangeType() == ContextChange.ChangeType.ADDITION){
                if(newChange.getChangeType() == rcType){
                    rcFlag = true;
                }
                long oldTime = System.nanoTime();
                for(RuntimeNode runtimeNode : runtimeNodeSet){
                    runtimeNode.vtPropagationAdd(newChange.getContext());
                }
            }
            else{
                if(newChange.getChangeType() == rcType){
                    for(RuntimeNode runtimeNode : runtimeNodeSet){
                        HashMap<Context, RuntimeNode.Virtual_Truth_Type> kidsVT = runtimeNode.getKidsVT();
                        RuntimeNode.Virtual_Truth_Type kidVT = kidsVT.get(newChange.getContext());
                        if(kidVT != null && rcSet.get(ContextChange.ChangeType.DELETION).contains(kidVT)){
                            rcFlag = true;
                            break;
                        }
                    }
                }
                for(RuntimeNode runtimeNode : runtimeNodeSet){
                    HashMap<Context, RuntimeNode.Virtual_Truth_Type> kidsVT = runtimeNode.getKidsVT();
                    RuntimeNode.Virtual_Truth_Type kidVT = kidsVT.get(newChange.getContext());
                    if(kidVT != null){
                        runtimeNode.vtPropagationDelete(kidVT, newChange.getContext());
                    }
                }
            }
            rule.setRiskAlready(rcFlag);
            return false;
        }
    }

    private void riskRefresh(Rule rule){
        rule.setRiskAlready(false);
        //根据batch中第一个修改virtualTruth
        boolean ret = riskMatch(rule, rule.getBatch().get(0));
        assert !ret;
    }

    public void checkEnds() throws NotSupportedException {
        CleanUp();
        switch (this.checker.getTechnique()) {
            case "ConC":
                ((ConC) checker).ThreadPool.shutdown();
                break;
            case "CPCC_NB":
                ((INFUSE_C) checker).ThreadPool.shutdown();
                break;
            case "BASE":
                assert this.checker instanceof ConC;
                ((ConC) checker).ThreadPool.shutdown();
                break;
        }
    }

    protected void CleanUp() throws NotSupportedException {
        //最后一次检测
        for(Rule rule : ruleMap.values()){
            if(rule.getBatch() != null){
                this.checker.ctxChangeCheckBatch(rule, rule.getBatch());
                rule.setBatch(null);
            }
        }
    }

    @Override
    public String getOutputInfo(String ruleType) {
        return null;
    }

}
