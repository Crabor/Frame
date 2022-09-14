package platform.service.ctx.INFuse.Middleware.Schedulers;

import platform.service.ctx.INFuse.Constraints.Formulas.FExists;
import platform.service.ctx.INFuse.Constraints.Formulas.FForall;
import platform.service.ctx.INFuse.Constraints.Formulas.Formula;
import platform.service.ctx.INFuse.Constraints.Rule;
import platform.service.ctx.INFuse.Constraints.RuleHandler;
import platform.service.ctx.INFuse.Constraints.Runtime.RuntimeNode;
import platform.service.ctx.INFuse.Contexts.Context;
import platform.service.ctx.INFuse.Contexts.ContextChange;
import platform.service.ctx.INFuse.Contexts.ContextPool;
import platform.service.ctx.INFuse.Middleware.Checkers.Checker;
import platform.service.ctx.INFuse.Middleware.Checkers.ConC;
import platform.service.ctx.INFuse.Middleware.Checkers.INFUSE_C;
import platform.service.ctx.INFuse.Middleware.NotSupportedException;

import java.util.*;

public class INFUSE_S extends Scheduler{


    public INFUSE_S(RuleHandler ruleHandler, ContextPool contextPool, Checker checker) {
        super(ruleHandler, contextPool, checker);
        this.strategy = "INFUSE_S";
    }

    @Override
    public void doSchedule(ContextChange contextChange) throws Exception {
        Batch_Form_DIS(contextChange);
        for(Rule rule : ruleHandler.getRuleList()){
            if(rule.getNewBatch() != null){
                this.checker.ctxChangeCheckBatch(rule, rule.getBatch());
                rule.setBatch(rule.getNewBatch());
                rule.setNewBatch(null);
                this.riskRefresh(rule);
            }
        }
    }

    private void Batch_Form_DIS(ContextChange newChange){
        for(Rule rule : ruleHandler.getRuleList()){
            if(!rule.getRelatedPatterns().contains(newChange.getPattern_id()))
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
        String pattern_id = newChange.getPattern_id();
        Formula formula = rule.getPatToFormula().get(pattern_id);
        Map<ContextChange.Change_Type, Set<RuntimeNode.Virtual_Truth_Type>> rcSet = null;
        Map<ContextChange.Change_Type, Set<RuntimeNode.Virtual_Truth_Type>> reSet = null;
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
            ContextChange.Change_Type reType = reSet.containsKey(ContextChange.Change_Type.ADDITION) ? ContextChange.Change_Type.ADDITION : ContextChange.Change_Type.DELETION;
            Set<RuntimeNode> runtimeNodeSet = rule.getPatToRuntimeNode().get(pattern_id);
            boolean reFlag = false;
            if(newChange.getChange_type() == ContextChange.Change_Type.ADDITION){
                if(newChange.getChange_type() == reType){
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
                if(newChange.getChange_type() == reType){
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
                            if(change.getChange_type() == ContextChange.Change_Type.ADDITION &&
                                change.getPattern_id().equals(pattern_id) &&
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
            ContextChange.Change_Type rcType = rcSet.containsKey(ContextChange.Change_Type.ADDITION) ? ContextChange.Change_Type.ADDITION : ContextChange.Change_Type.DELETION;
            Set<RuntimeNode> runtimeNodeSet = rule.getPatToRuntimeNode().get(pattern_id);
            boolean rcFlag = false;
            if(newChange.getChange_type() == ContextChange.Change_Type.ADDITION){
                if(newChange.getChange_type() == rcType){
                    rcFlag = true;
                }
                long oldTime = System.nanoTime();
                for(RuntimeNode runtimeNode : runtimeNodeSet){
                    runtimeNode.vtPropagationAdd(newChange.getContext());
                }
            }
            else{
                if(newChange.getChange_type() == rcType){
                    for(RuntimeNode runtimeNode : runtimeNodeSet){
                        HashMap<Context, RuntimeNode.Virtual_Truth_Type> kidsVT = runtimeNode.getKidsVT();
                        RuntimeNode.Virtual_Truth_Type kidVT = kidsVT.get(newChange.getContext());
                        if(kidVT != null && rcSet.get(ContextChange.Change_Type.DELETION).contains(kidVT)){
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
        for(Rule rule : ruleHandler.getRuleList()){
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