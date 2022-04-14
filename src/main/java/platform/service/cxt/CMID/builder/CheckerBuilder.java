package platform.service.cxt.CMID.builder;

import platform.service.cxt.CMID.checker.Checker;
import platform.service.cxt.Config.PlatformConfig;
import platform.service.cxt.Context.ContextManager;

import java.util.*;

import static java.lang.Thread.sleep;

/**
 * Created by njucjc on 2017/10/23.
 */
public class CheckerBuilder  extends AbstractCheckerBuilder implements Runnable{


    public CheckerBuilder(PlatformConfig config) {
        super(config);
    }
    @Override
    public void run() {
        //List<String> contextList = fileReader(this.dataFilePath);
        System.out.println("[INFO] 开始一致性处理");
        int count = 0;
        long timeSum = 0L;

        Map<Long, List<String>> deleteChanges = new TreeMap<>();

        int chg_count = 0;
        //for(String line : contextList) {
        while (true){
            if (!PlatformConfig.getInstace().isCtxCleanOn())
                continue;
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LinkedList<String> results = ContextManager.getChangeInvokedElements();
            // obtained changes received from blockingList in platform.ContextManager

            System.out.print("[INFO] 当前进度: " + results.size() + '\r');
            //System.out.println("Line:" + line);
            //Context context = ContextParser.jsonToContext(count, line);
            //System.out.println("Context:" + context.toString());
            //List<String> changes = ChangeHelper.toChanges(context, deleteC hanges, patternList);

            long startTime = System.nanoTime();
            for (String chg : results) {
                System.out.println(chg);
                //changeHandler.doContextChange(chg);
            }
            long endTime = System.nanoTime(); //获取结束时间
            timeSum += (endTime - startTime);

            count = results.size() + count;
        }

       // long start = System.nanoTime();
        /*Iterator<Map.Entry<Long, List<String>>> it = deleteChanges.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, List<String>> entry = it.next();
            long key = entry.getKey();
            for (String chg : deleteChanges.get(key)) {
                changeHandler.doContextChange(chg);
            }
        }*/
        /*long end = System.nanoTime();
        timeSum += (end - start);

        int incCount = 0;
        for(Checker checker : checkerList) {
            incCount += checker.getInc();
        }
        System.out.println();

        LogFileHelper.getLogger().info("[INFO] Total INC: " + incCount, true);
        LogFileHelper.getLogger().info("[INFO] Total checking time: " + timeSum / 1000000 + " ms", true);
        accuracy(LogFileHelper.logFilePath);
        shutdown();*/

    }


}
