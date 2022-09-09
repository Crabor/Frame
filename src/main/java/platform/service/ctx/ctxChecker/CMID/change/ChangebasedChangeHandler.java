package platform.service.ctx.ctxChecker.CMID.change;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import platform.service.ctx.ctxChecker.CMID.checker.Checker;
import platform.service.ctx.ctxChecker.CMID.context.ContextParser;
import platform.service.ctx.ctxChecker.CMID.pattern.Pattern;
import platform.service.ctx.ctxChecker.CMID.scheduler.Scheduler;

import java.util.List;
import java.util.Map;

/**
 * Created by njucjc at 2018/1/23
 */
public class ChangebasedChangeHandler extends ChangeHandler {
    private static final Log logger = LogFactory.getLog(ChangebasedChangeHandler.class);
    public ChangebasedChangeHandler(Map<String, Pattern> patternMap, Map<String, Checker> checkerMap, Scheduler scheduler, List<Checker> checkerList) {
        super(patternMap, checkerMap, scheduler, checkerList);
    }

    @Override
    public void doContextChange(String change) {
        scheduler.update(change);
        doCheck();

        String [] strs = change.split(",");

        String op = strs[0];
        String patternId = strs[2]; // patternID

        if (op.equals("+")) {
            additionChange(patternId, ContextParser.parseChangeFromPlatfrom(strs));
        }
        else if (op.equals("-")) {
            deleteChange(ContextParser.parseChangeFromPlatfrom(strs).getTimestamp(), patternId);
        }
        else {
            logger.info("存在不可识别操作类型" + op);
            System.exit(1);
        }

    }
}
