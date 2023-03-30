package platform.service.inv;

import com.alibaba.fastjson.JSONObject;
import common.socket.CmdMessageGrpIds;
import common.socket.UDP;
import common.struct.CheckInfo;
import common.struct.InvServiceConfig;
import common.struct.enumeration.CheckResult;
import common.struct.enumeration.SensorDataType;
import common.struct.sync.SynchronousJsonObject;
import common.struct.sync.SynchronousSensorData;
import common.struct.sync.SynchronousString;
import common.util.Util;
import platform.communication.pubsub.Publisher;
import platform.config.AppConfig;
import platform.config.Configuration;
import platform.config.SensorConfig;
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
    private final SynchronousSensorData _invGetSensorData = new SynchronousSensorData();
//    String[] envContextNames;

    public AppInvServer(AppConfig appConfig, InvServiceConfig config) {
        this.appConfig = appConfig;
        this.config = config;
//        envContextNames = Configuration.getResourceConfig().getSensorsConfig().keySet().toArray(new String[0]);
    }

    public SynchronousSensorData getInvGetSensorData() {
        return _invGetSensorData;
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
        Map<String, SensorConfig> sensorConfigMap = Configuration.getResourceConfig().getSensorsConfig();
        while (true) {
            JSONObject jo = checkChannel.blockTake();
            int lineNumber = jo.getIntValue("line_number");
            long checkTime = jo.getLongValue("check_time");
            int iterId = jo.getIntValue("iter_id");
            JSONObject objs = jo.getJSONObject("objs");

            if (!lines.containsKey(lineNumber)) {
                String[] checkNames = objs.keySet().toArray(new String[0]);
                lines.put(lineNumber, new InvLine(config, checkNames));
            }

            InvLine line = lines.get(lineNumber);
            //TODO:获取环境上下文
            List<Double> tmp = new ArrayList<>();
            CmdMessageGrpIds send = new CmdMessageGrpIds("sensory_request", null, List.of(appConfig.getGrpId()));
            sensorConfigMap.forEach((sensorName, sensorConfig) -> {
                if (!appConfig.getRequestMap().containsKey(sensorName)) {
                    appConfig.getRequestMap().put(sensorName, new SynchronousString());
                }
                appConfig.getRequestMap().get(sensorName).put("invGetSensorData");
                Publisher.publish(sensorName + "_request", send.toString());
                _invGetSensorData.blockTake().getAllData().values().forEach(v -> tmp.add((Double) v));
            });
            double[] envCtxVals = tmp.stream().mapToDouble(Double::doubleValue).toArray();
            double[] checkVals = Util.toDoubleArray(objs.values());
            CheckResult result = line.check(new InvData(envCtxVals, checkVals));
            String name = objs.keySet().toString();

            //TODO:返回checkInfo给appDriver并传递给app
            JSONObject joo = new JSONObject();
            joo.put("sensor_data_type", SensorDataType.INV_REPORT);
            joo.put("name", name);
            joo.put("line_number", lineNumber);
            joo.put("iter_id", iterId);
            joo.put("check_time", checkTime);
            joo.put("result", result);
            JSONObject jooo = new JSONObject(2);
            jooo.put("channel", name);
            jooo.put("msg", joo);
            UDP.send(appConfig.getAppDriver().getClientIP(), appConfig.getAppDriver().getClientUDPPort(), jooo.toJSONString());
        }
    }
}
