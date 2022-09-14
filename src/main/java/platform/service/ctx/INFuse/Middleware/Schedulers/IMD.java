package platform.service.ctx.INFuse.Middleware.Schedulers;


import platform.service.ctx.INFuse.Constraints.RuleHandler;
import platform.service.ctx.INFuse.Contexts.ContextChange;
import platform.service.ctx.INFuse.Contexts.ContextPool;
import platform.service.ctx.INFuse.Middleware.Checkers.Checker;
import platform.service.ctx.INFuse.Middleware.Checkers.ConC;
import platform.service.ctx.INFuse.Middleware.Checkers.INFUSE_C;


public class IMD extends Scheduler{


    public IMD(RuleHandler ruleHandler, ContextPool contextPool, Checker checker) {
        super(ruleHandler, contextPool, checker);
        this.strategy = "IMD";
    }

    @Override
    public void doSchedule(ContextChange contextChange) throws Exception {
        this.checker.ctxChangeCheckIMD(contextChange);
    }

    @Override
    public void checkEnds() throws Exception {
        switch (this.checker.getTechnique()) {
            case "ConC":
                ((ConC) this.checker).ThreadPool.shutdown();
                break;
            case "CPCC_NB":
                ((INFUSE_C) this.checker).ThreadPool.shutdown();
                break;
            case "BASE":
                assert this.checker instanceof ConC;
                ((ConC) this.checker).ThreadPool.shutdown();
                break;
        }
    }

    @Override
    public String getOutputInfo(String ruleType) {
       return null;
    }
}
