package platform.service.ctx.CMID.scheduler;

import platform.service.ctx.CMID.checker.Checker;
import platform.service.ctx.CMID.context.Context;
import platform.service.ctx.CMID.context.ContextParser;

import java.util.List;

public class GEASOptScheduler extends GEAScheduler {

    public GEASOptScheduler(List<Checker> checkerList) {
        super(checkerList);
    }

    @Override
    protected String cCondition(Checker checker, List<String> currentBatch, String[] elements, List<Boolean> subTree) {


        for (int i = 0; i < currentBatch.size(); i++) {
            String c = currentBatch.get(i);
            String [] e = c.split(",");
            if (e[0].equals(elements[0]) || !e[1].equals(elements[1])) { //+ and -, same pattern
                continue;
            }

            if(checker.inCriticalSet(e[2]) || checker.inCriticalSet(elements[2])) {// part(3)
                continue;
            }

            Context c1 = ContextParser.parseChangeContext(elements);
            Context c2 = ContextParser.parseChangeContext(e);

            if (checker.allEqual(e[1], c1, c2)) {
                return c;
            }
        }

        return null;
    }

    @Override
    protected List<Boolean> calcSubTree(Checker checker, String patternId, Context c) {
        return null;
    }

    @Override
    protected void sCheck(Checker checker) {
        return;
    }
}