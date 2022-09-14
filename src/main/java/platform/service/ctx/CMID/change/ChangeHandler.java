package platform.service.ctx.CMID.change;

import platform.service.ctx.CMID.checker.Checker;
import platform.service.ctx.CMID.context.Context;
import platform.service.ctx.CMID.pattern.Pattern;
import platform.service.ctx.CMID.scheduler.Scheduler;

import java.util.List;
import java.util.Map;

/**
 * Created by njucjc at 2018/1/23
 */
public abstract class ChangeHandler {
    protected Map<String, Pattern> patternMap;

    protected Map<String, Checker> checkerMap;

    protected Scheduler scheduler;

    protected List<Checker> checkerList;

    public long timeCount = 0L;

    public ChangeHandler(Map<String, Pattern> patternMap, Map<String, Checker> checkerMap, Scheduler scheduler, List<Checker> checkerList) {
        this.patternMap = patternMap;
        this.checkerMap = checkerMap;
        this.scheduler = scheduler;
        this.checkerList = checkerList;

    }

    private String getClassString(String str) {
        return str.substring(str.lastIndexOf(".") + 1);
    }

    public void doCheck() {
        long start = System.nanoTime();
        for(Checker checker : checkerList) {
            if(scheduler.schedule(checker.getName())) {
                checker.doCheck();
            }
        }
        long end = System.nanoTime();
        timeCount += (end -start);
    }

    protected final void deleteChange(long timestamp, String  patternId) {
        Pattern pattern = patternMap.get(patternId);
        if (pattern == null) {
            System.exit(1);
        }
        Checker checker = checkerMap.get(patternId);
        //System.out.println(checkerMap.keySet().toString());
        //System.out.println("patternID:"+patternId);
        checker.delete(patternId, timestamp);
     //   pattern.deleteFirstByTime(timestamp);
    }

    protected final void additionChange(String patternId, Context context) {
        Pattern pattern = patternMap.get(patternId);
        if (pattern == null) {
            System.exit(1);
        }
        if(pattern.isBelong(context)) {
    //        pattern.addContext(context);
            Checker checker = checkerMap.get(pattern.getId());
            if(checker!=null) {
             //   System.out.println(checker.getName());
                checker.add(pattern.getId(), context);
            }
        }
    }

    public abstract void doContextChange(String change);

    public void shutdown() {}

    public void update(Map<String, Checker> checkerMap, Scheduler scheduler, List<Checker> checkerList) {
        this.checkerMap = checkerMap;
        this.scheduler = scheduler;
        this.checkerList = checkerList;
    }

}
