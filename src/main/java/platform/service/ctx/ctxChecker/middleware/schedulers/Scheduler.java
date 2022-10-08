package platform.service.ctx.ctxChecker.middleware.schedulers;

import platform.service.ctx.ctxChecker.context.ContextChange;
import platform.service.ctx.ctxChecker.context.ContextPool;
import platform.service.ctx.ctxChecker.middleware.checkers.Checker;

public abstract class Scheduler {
    protected String strategy;
    protected ContextPool contextPool;
    protected Checker checker;

    public Scheduler(ContextPool contextPool, Checker checker){
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
