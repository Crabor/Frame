package platform.service.inv;

import com.alibaba.fastjson.JSONObject;
import common.struct.InvServiceConfig;
import common.struct.sync.SynchronousJsonObject;
import common.util.Util;
import platform.config.AppConfig;
import platform.service.inv.struct.InvData;
import platform.service.inv.struct.InvLine;

import java.io.File;
import java.util.*;

public class AppInvServer implements Runnable {
    private final AppConfig appConfig;
    private final Set<String> monitoredObjs = new HashSet<>();
    private final SynchronousJsonObject checkChannel = new SynchronousJsonObject();
    private final Map<Integer, InvLine> lines = new HashMap<>();
    private final InvServiceConfig config;
//    String[] envContextNames;

    public AppInvServer(AppConfig appConfig, InvServiceConfig config) {
        this.appConfig = appConfig;
        this.config = config;
//        envContextNames = Configuration.getResourceConfig().getSensorsConfig().keySet().toArray(new String[0]);
    }

    public void start() {
        File dir = new File("output/platform/service/inv/" + appConfig.getAppName());
        Util.deleteDir(dir);
        dir.mkdirs();
        File traceDir = new File("output/platform/service/inv/" + appConfig.getAppName() + "/trace");
        traceDir.mkdirs();
        File invDir = new File("output/platform/service/inv/" + appConfig.getAppName() + "/inv");
        invDir.mkdirs();
    }

    public void stop() {
        File dir = new File("output/platform/service/inv/" + appConfig.getAppName());
        Util.deleteDir(dir);
    }

    public boolean monitor(List<String> objs) {
        monitoredObjs.addAll(objs);
        return true;
    }

    public boolean isMonitored(List<String> objs) {
        return monitoredObjs.containsAll(objs);
    }

    public boolean check(JSONObject jo) {
        //TODO
        checkChannel.put(jo);
        return true;
    }

    @Override
    public void run() {
        while (true) {
            JSONObject jo = checkChannel.blockTake();
            int lineNumber = jo.getIntValue("line_number");
            long checkTime = jo.getLongValue("check_time");
            JSONObject objs = jo.getJSONObject("objs");

            if (!lines.containsKey(lineNumber)) {
                String[] checkNames = objs.keySet().toArray(new String[0]);
                lines.put(lineNumber, new InvLine(config, checkNames));
            }

            InvLine line = lines.get(lineNumber);
            //TODO:获取环境上下文
            double[] envCtxVals = new double[10];
            double[] checkVals = Util.toDoubleArray(objs.values());
            line.check(new InvData(envCtxVals, checkVals));

            //TODO:返回checkInfo给appDriver并传递给app
        }
    }
}
