package platform.service.cxt.CMID.change;

import platform.service.cxt.CMID.checker.Checker;
import platform.service.cxt.CMID.context.ContextParser;
import platform.service.cxt.CMID.pattern.Pattern;
import platform.service.cxt.CMID.scheduler.Scheduler;

import java.util.List;
import java.util.Map;

/**
 * Created by njucjc at 2018/1/23
 */
public class ChangebasedChangeHandler extends ChangeHandler {
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
            System.out.println("[INFO] 存在不可识别操作类型" + op);
            System.exit(1);
        }

    }
}
