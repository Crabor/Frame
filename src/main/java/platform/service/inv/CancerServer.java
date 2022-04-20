package platform.service.inv;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import platform.app.AppMgrThread;
import platform.pubsub.AbstractSubscriber;
import platform.resource.ResMgrThread;
import platform.service.inv.struct.CheckInfo;
import platform.service.inv.struct.SegInfo;
import reactor.util.annotation.Nullable;

import java.util.*;

public class CancerServer extends AbstractSubscriber implements Runnable {
    private static CancerServer instance;
    private Thread t;
    private int iterId = 0;

    //静态变量，第一维为appName，第二维为iterId，第三维为LineNumber，第四维为保存的checkInfo列表
    private static final Map<String, Map<Integer, Map<Integer, List<CheckInfo>>>> checkMap = new HashMap<>();
    //静态变量，第一维为appName，第二维为iterId，第三维为保存的segInfo
    private static final Map<String, Map<Integer, SegInfo>> segMap = new HashMap<>();

    // 构造方法私有化
    private CancerServer() {
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

        }
    }

    public void start() {
        if (t == null) {
            t = new Thread(this, getClass().getSimpleName());
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
            AppMgrThread.getInstance().getAppNames().forEach(appName -> {
                if (!segMap.containsKey(appName)) {
                    segMap.put(appName, new HashMap<>());
                }
                Map<Integer, SegInfo> iterMap = segMap.get(appName);
                if (!iterMap.containsKey(iterId)) {
                    iterMap.put(iterId, new SegInfo(iterId));
                }
                SegInfo segInfo = iterMap.get(iterId);
                segInfo.eCxt = map;
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
    }
}
