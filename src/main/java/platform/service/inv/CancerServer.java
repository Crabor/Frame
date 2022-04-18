package platform.service.inv;

import com.alibaba.fastjson.JSONArray;
import platform.pubsub.AbstractSubscriber;
import platform.resource.ResMgrThread;
import platform.service.inv.struct.CheckInfo;
import platform.service.inv.struct.SegInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CancerServer extends AbstractSubscriber implements Runnable {
    private static CancerServer instance;
    private Thread t;
    private int iterId = 0;
    //静态变量，第一维为appName，第二维为iterId，第三维为LineNumber，第四维为保存的checkInfo列表
    private static final Map<String, Map<Integer, Map<Integer, List<CheckInfo>>>> checkMap = new HashMap<>();
    //静态变量，第一维为appName，第二维为iterId，第三维为保存的segInfo列表
    private static final Map<String, Map<Integer, SegInfo>> segMap = new HashMap<>();

    // 构造方法私有化
    private CancerServer() {
    }

    // 静态方法返回该实例
    public static CancerServer getInstance() {
        if(instance == null) {
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

    }

    public void start() {
        if (t == null) {
            t = new Thread (this, getClass().getName());
            t.start ();
        }
    }

    @Override
    public void onMessage(String channel, String msg) {

        if (channel.equals("check")) {
            // TODO :
            JSONArray jsonArray = JSONArray.parseArray(msg);
            for (int i = 0; i < jsonArray.size(); i++) {
                CheckInfo checkInfo = jsonArray.getObject(i, CheckInfo.class);
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
            System.out.println("inv recv: " + msg);
        }
    }

    @Override
    public void onSubscribed(String channel, long subChannelCount) {

    }

    @Override
    public void onUnsubscribed(String channel, long subChannelCount) {

    }


}
