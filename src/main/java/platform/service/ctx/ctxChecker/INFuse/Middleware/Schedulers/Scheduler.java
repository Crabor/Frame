package platform.service.ctx.ctxChecker.INFuse.Middleware.Schedulers;

import platform.service.ctx.ctxChecker.INFuse.Constraints.RuleHandler;
import platform.service.ctx.ctxChecker.INFuse.Contexts.ContextChange;
import platform.service.ctx.ctxChecker.INFuse.Contexts.ContextPool;
import platform.service.ctx.ctxChecker.INFuse.Middleware.Checkers.Checker;

public abstract class Scheduler {
    protected String strategy;
    protected RuleHandler ruleHandler;
    protected ContextPool contextPool;
    protected Checker checker;

    public Scheduler(RuleHandler ruleHandler, ContextPool contextPool, Checker checker){
        this.ruleHandler = ruleHandler;
        this.contextPool = contextPool;
        this.checker = checker;
    }

    public abstract void doSchedule(ContextChange contextChange) throws Exception;
    public abstract void checkEnds() throws Exception;
    public abstract String getOutputInfo(String ruleType);

    public Checker getChecker() {
        return checker;
    }
}
