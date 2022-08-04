package platform.service.inv;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import platform.app.AppMgrThread;
import platform.pubsub.AbstractSubscriber;
import platform.config.Configuration;
import platform.service.inv.algorithm.*;
import platform.service.inv.struct.*;
import platform.service.inv.struct.inv.InvAbstract;
import platform.service.inv.struct.trace.Trace;
import platform.struct.InvGenMode;
import platform.util.Util;

import java.io.File;
import java.util.*;

public class CancerServer extends AbstractSubscriber implements Runnable {
    private static CancerServer instance;
    private Thread t;
    private int iterId = 0;

    //静态变量，第一维为appName，第二维为iterId，第三维为LineNumber，第四维为保存的checkInfo列表
    private static final Map<String, Map<Integer, Map<Integer, List<CheckInfo>>>> checkMap = new HashMap<>();
    //静态变量，第一维为appName，第二维为iterId，第三维为保存的segInfo
    private static final Map<String, Map<Integer, SegInfo>> segMap = new HashMap<>();
    private static final Map<String, PECount> peCountMap = new HashMap<>();
    //静态变量，第一维为appName，第二维为lineNumber，第三维为cancerObject列表
    private static final Map<String, Map<Integer, List<CancerObject>>> lineMap = new HashMap<>();
    
    private static final Log logger = LogFactory.getLog(CancerServer.class);

    // 构造方法私有化
    private CancerServer() {
        File dir = new File("output/inv/");
        Util.deleteDir(dir);
        dir.mkdirs();
    }

    // 静态方法返回该实例
    public static CancerServer getInstance() {
        if (instance == null) {
            synchronized (CancerServer.class) {
                if (instance == null) {
                    instance = new CancerServer();
                }
            }
        }
        return instance;
    }

    @Override
    public void run() {
        //group
        while (true) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (Configuration.getCancerServerConfig().getInvGenMode() == InvGenMode.TOTAL) {
                segMap.forEach((appName, iterMap) -> {
                    PECount peCount = peCountMap.get(appName);
                    if (!peCount.isGrouped &&
                            Math.min(peCount.eCxtCount, peCount.pCxtCount)
                                    > Configuration.getCancerServerConfig().getGroupThro()) {
                        peCount.isGrouped = true;
                        //group
                        KMeans kMeans = new KMeans(
                                new ArrayList<>(iterMap.values()).subList(0, Configuration.getCancerServerConfig().getGroupThro()),
                                Configuration.getCancerServerConfig().getKMeansGroupSize(),
                                1E-10,
                                Configuration.getListOfSensorObj().size());
                        kMeans.run();
                        DoS dos = new DoS(
                                iterMap,
                                kMeans.getGrps(),
                                Configuration.getCancerServerConfig().getDosThro());
                        dos.run();

                        logger.info(appName + " group:");
                        dos.getOutGrps().forEach((grp, iters) -> {
                            logger.info(grp + "=" + iters);
                        });

                        //output group trace & gen inv
                        Trace traceOutput =  Configuration.getCancerServerConfig().getGroupTraceType();
                        dos.getOutGrps().forEach((grp, iters) -> {
                            Map<Integer, List<Integer>> linesTrace = new HashMap<>();
                            iters.forEach(iter -> {
                                segMap.get(appName).get(iter).pCxt.forEach(lineNumber -> {
                                    if (!linesTrace.containsKey(lineNumber)) {
                                        linesTrace.put(lineNumber, new ArrayList<>());
                                    }
                                    linesTrace.get(lineNumber).add(iter);
                                });
                            });
                            linesTrace.forEach((lineNumber, lineTrace) -> {
                                // trace output
                                traceOutput.printTrace(appName, lineNumber, grp, segMap.get(appName), iters);
                                // inv meta info
                                lineMap.get(appName).get(lineNumber).forEach(cancerObject -> {
                                    if (!cancerObject.getInvMap().containsKey(lineNumber)) {
                                        cancerObject.getInvMap().put(lineNumber, new HashMap<>());
                                    }
                                    if (!cancerObject.getInvMap().get(lineNumber).containsKey(grp)) {
                                        String invClassName = "platform.service.inv.struct.inv.Inv" +
                                                Util.makeFirstCharUpperCase(Configuration.getCancerServerConfig().getInvGenType().toString().toLowerCase());
                                        try {
                                            InvAbstract inv = (InvAbstract) Class.forName(invClassName).newInstance();
                                            inv.setMetaData(appName, lineNumber, grp, cancerObject.getName(), lineTrace);
                                            cancerObject.getInvMap().get(lineNumber).put(grp, inv);
                                        } catch (InstantiationException |
                                                 IllegalAccessException |
                                                 ClassNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    //inv gen
                                    InvAbstract inv = cancerObject.getInvMap().get(lineNumber).get(grp);
                                    inv.setState(InvState.INV_GENERATING);
                                    inv.genInv();
                                    inv.setState(InvState.INV_GENERATED);
                                });
                            });
                        });
                    }
                });
            }

        }
    }

    public void start() {
        if (t == null) {
            t = new Thread(this, getClass().getName());
            t.start();
        }
    }

    @Override
    public void onMessage(String channel, String msg) {
        if (channel.equals("check")) {
            List<CheckInfo> list = JSONArray.parseArray(msg, CheckInfo.class);
            for (CheckInfo checkInfo : list) {
                if (!checkMap.containsKey(checkInfo.appName)) {
                    checkMap.put(checkInfo.appName, new HashMap<>());
                }
                Map<Integer, Map<Integer, List<CheckInfo>>> iterMap = checkMap.get(checkInfo.appName);
                if (!iterMap.containsKey(checkInfo.iterId)) {
                    iterMap.put(checkInfo.iterId, new HashMap<>());
                }
                Map<Integer, List<CheckInfo>> lineMap = iterMap.get(checkInfo.iterId);
                if (!lineMap.containsKey(checkInfo.lineNumber)) {
                    lineMap.put(checkInfo.lineNumber, new ArrayList<>());
                }
                List<CheckInfo> checkInfoList = lineMap.get(checkInfo.lineNumber);
                checkInfoList.add(checkInfo);
            }
        } else if (channel.equals("sensor")) {
            iterId++;
            Map<String, Double> map = new HashMap<>();
            JSONObject jsonObject = JSONObject.parseObject(msg);
            for (String key : jsonObject.keySet()) {
                map.put(key, jsonObject.getDouble(key));
            }
            AppMgrThread.getInstance().getApps().forEach(app -> {
                String appName = ((AbstractSubscriber) app).getName();
                if (!segMap.containsKey(appName)) {
                    segMap.put(appName, new HashMap<>());
                }
                Map<Integer, SegInfo> iterMap = segMap.get(appName);
                if (!iterMap.containsKey(iterId)) {
                    iterMap.put(iterId, new SegInfo(iterId));
                }
                SegInfo segInfo = iterMap.get(iterId);
                segInfo.eCxt = map;
                if (!peCountMap.containsKey(appName)) {
                    peCountMap.put(appName, new PECount());
                }
                PECount peCount = peCountMap.get(appName);
                peCount.eCxtCount++;
            });
        }
    }

    @Override
    public void onSubscribed(String channel, long subChannelCount) {

    }

    @Override
    public void onUnsubscribed(String channel, long subChannelCount) {

    }

    public static Map<String, Map<Integer, Map<Integer, List<CheckInfo>>>> getCheckMap() {
        return checkMap;
    }

    public static Map<String, Map<Integer, SegInfo>> getSegMap() {
        return segMap;
    }

    public static Map<String, PECount> getPECountMap() {
        return peCountMap;
    }

    public static Map<String, Map<Integer, List<CancerObject>>> getLineMap() {
        return lineMap;
    }

    public static void iterEntry(String appName, int iterId) {
        CancerObject.iterEntry(appName, iterId);
    }

    public static void iterExit(String appName, int iterId) {
        if (!checkMap.containsKey(appName)) {
            checkMap.put(appName, new HashMap<>());
        }
        Map<Integer, Map<Integer, List<CheckInfo>>> iterMap = checkMap.get(appName);
        if (!iterMap.containsKey(iterId)) {
            iterMap.put(iterId, new HashMap<>());
        }
        Map<Integer, List<CheckInfo>> lineMap = iterMap.get(iterId);
        if (!segMap.containsKey(appName)) {
            segMap.put(appName, new HashMap<>());
        }
        Map<Integer, SegInfo> iterMap1 = segMap.get(appName);
        if (!iterMap1.containsKey(iterId)) {
            iterMap1.put(iterId, new SegInfo(iterId));
        }
        SegInfo segInfo = iterMap1.get(iterId);

        segInfo.pCxt = lineMap.keySet();
        segInfo.checkTable = lineMap;

        if (!peCountMap.containsKey(appName)) {
            peCountMap.put(appName, new PECount());
        }
        PECount peCount = peCountMap.get(appName);
        peCount.pCxtCount++;
    }
}
