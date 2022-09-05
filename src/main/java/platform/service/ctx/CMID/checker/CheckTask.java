package platform.service.ctx.CMID.checker;

import platform.service.ctx.CMID.context.Context;
import platform.service.ctx.CMID.node.CCTNode;

import java.util.List;
import java.util.concurrent.Callable;

public class CheckTask implements Callable<Result> {
    private CCTNode subcctRoot;

    private List<Context> param;

    private Checker checker;

    private int start;

    private int end;

    public CheckTask(CCTNode subcctRoot, List<Context> param, Checker checker, int start, int end) {
        this.subcctRoot = subcctRoot;
        this.param = param;
        this.checker = checker;
        this.start = start;
        this.end = end;
    }

    @Override
    public Result call() {
        if (subcctRoot.getNodeType() == CCTNode.UNIVERSAL_NODE) {
            return checker.universalNodeEval(subcctRoot, param, start, end);
        } else {
            return checker.existentialNodeEval(subcctRoot, param, start, end);
        }

    }


    public void setSubcctRoot(CCTNode subcctRoot) {
        this.subcctRoot = subcctRoot;
    }


    public void setParam(List<Context> param) {
        this.param = param;
    }
}
