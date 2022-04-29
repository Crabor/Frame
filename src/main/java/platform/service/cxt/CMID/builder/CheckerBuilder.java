package platform.service.cxt.CMID.builder;

import com.alibaba.fastjson.JSONObject;
import platform.service.cxt.CMID.checker.Checker;
import platform.service.cxt.Config.PlatformConfig;
import platform.service.cxt.Context.Change;
import platform.service.cxt.Context.ContextManager;

import java.util.*;

import static java.lang.Thread.sleep;
import static platform.service.cxt.Context.ContextManager.msgStatistics;

/**
 * Created by njucjc on 2017/10/23.
 */
public class CheckerBuilder  extends AbstractCheckerBuilder implements Runnable{

    private static HashMap<String, Long> checkPointLog;

    public CheckerBuilder(PlatformConfig config) {
        super(config);
        checkPointLog = new HashMap<>();
        for(int i = 0; i<patternList.size(); i++)
            checkPointLog.put(patternList.get(i).getId(), 0L);
    }
    @Override
    public void run() {
        //List<String> contextList = fileReader(this.dataFilePath);
        System.out.println("[INFO] 开始一致性处理");
        int count = 0;
        long timeSum = 0L;


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


            //System.out.print("[INFO] 当前进度: " + results.size() + '\r');
            //System.out.println("Line:" + line);
            //Context context = ContextParser.jsonToContext(count, line);
            //System.out.println("Context:" + context.toString());
            //List<String> changes = ChangeHelper.toChanges(context, deleteC hanges, patternList);

            long startTime = System.nanoTime();
            for (String chg : results) {
                //System.out.println("Checking: "+chg);
                changeHandler.doContextChange(chg);

                if(chg.startsWith("+")) {
                    msgStatistics.addChk();
                    String[] parts = chg.split(","); //Checking: +,11,pat_right,5.264847945235763,2022-04-20 05:51:45
                    long index = Long.parseLong(parts[1]);
                    String pat = parts[2];
                    checkPointLog.put(pat, index);
                }
            }
            long endTime = System.nanoTime(); //获取结束时间
            timeSum += (endTime - startTime);

            count = results.size() + count;
            //System.out.println("[INFO] Total changes: "+count);
            //int incCount = 0;
            for(Checker checker : checkerList) {
                //incCount += checker.getInc();
                Set<String> incs_delta = checker.getIncLinkSet_delta();
                List<String> errorMsgIDs = incInvolvedMsgList(incs_delta);
                ContextManager.adderrorMsgIDList(errorMsgIDs);
                //System.out.println("Checker delta: "+checker.getName() + "====" + incs_delta.toString());
                //System.out.println("Error list: "+ errorMsgIDs.toString());
                //System.out.println("Checker: "+checker.getName() + "====" + checker.getIncLinkSet().toString());
                checker.clearIncLinkSet_delta();
            }
            long process_Tag = minInCheckPointLog();
            ContextManager.fixMsgElementsUntil(process_Tag);

            //System.out.println("[INFO] Total changes: "+incCount);
            //System.out.println("[INFO] Total checking time: " + timeSum / 1000000 + " ms");
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
    public Long minInCheckPointLog(){
        long temp = Long.MAX_VALUE;
        for(Map.Entry<String, Long> entry: checkPointLog.entrySet()) {
            Long value = entry.getValue();
            if (temp > value)
                temp = value;
        }
        return temp;
    }
    public List<String> incInvolvedMsgList(Set<String> inc_list){
        //[{"No":"9","SensorName":"pat_right","SensorData":"21.33015392339481","ID":"pat_right","TimeStamp":"1650487203000"},
        // {"No":"0","SensorName":"pat_right","SensorData":"22.371715751205922","ID":"pat_right","TimeStamp":"1650487201000"},
        // {"No":"2","SensorName":"pat_right","SensorData":"27.977156701164745","ID":"pat_right","TimeStamp":"1650487201000"}]
        List<String> list = new ArrayList<>();
        for(String str: inc_list){
            JSONObject json = JSONObject.parseObject(str);
            list.add(json.getString("No"));
        }
        return list;
    }

}
