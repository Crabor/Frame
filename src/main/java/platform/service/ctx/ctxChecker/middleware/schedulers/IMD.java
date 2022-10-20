package platform.service.ctx.ctxChecker.middleware.schedulers;


import platform.service.ctx.ctxChecker.context.ContextChange;
import platform.service.ctx.ctxChecker.context.ContextPool;
import platform.service.ctx.ctxChecker.middleware.checkers.Checker;
import platform.service.ctx.ctxChecker.middleware.checkers.ConC;
import platform.service.ctx.ctxChecker.middleware.checkers.INFUSE_C;


public class IMD extends Scheduler{


    public IMD(ContextPool contextPool, Checker checker) {
        super(contextPool, checker);
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
