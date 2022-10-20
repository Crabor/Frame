package platform.service.ctx.ctxChecker.middleware.schedulers;

import platform.service.ctx.rule.Rule;
import platform.service.ctx.ctxChecker.context.ContextChange;
import platform.service.ctx.ctxChecker.context.ContextPool;
import platform.service.ctx.ctxChecker.middleware.checkers.Checker;
import platform.service.ctx.ctxChecker.middleware.checkers.ConC;
import platform.service.ctx.ctxChecker.middleware.checkers.INFUSE_C;
import platform.service.ctx.ctxChecker.middleware.NotSupportedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GEAS_ori extends Scheduler{
    protected final Map<String, Rule> ruleMap;

    public GEAS_ori(Map<String, Rule> ruleMap, ContextPool contextPool, Checker checker) {
        super(contextPool, checker);
        this.ruleMap = ruleMap;
        this.strategy = "GEAS_ori";
    }

    @Override
    public void doSchedule(ContextChange contextChange) throws Exception {
        Batch_Form(contextChange);
        for(Rule rule : ruleMap.values()){
            if(rule.getNewBatch() != null){
                this.checker.ctxChangeCheckBatch(rule, rule.getBatch());
                rule.setBatch(rule.getNewBatch());
                rule.setNewBatch(null);
            }
        }
    }

    private void Batch_Form(ContextChange newChange){
        for(Rule rule : ruleMap.values()){
            if(!rule.getRelatedPatterns().contains(newChange.getPatternId()))
                continue;
            if(S_Condition_Match(rule, newChange)){
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

    protected boolean S_Condition_Match(Rule rule, ContextChange newChange) {
        boolean retflag = false;
        if(rule.getBatch() == null)
            return false;
        else{
            for(ContextChange change : rule.getBatch()){
                assert !rule.getIncType(change).equals("NotThisRule");
                assert !rule.getIncType(newChange).equals("NotThisRule");
                if(rule.getIncType(change).equals("Plus")){
                    if(rule.getIncType(newChange).equals("Minus")){
                        retflag = true;
                        break;
                    }
                }
            }
        }
        return retflag;
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

