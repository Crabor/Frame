package platform.service.inv;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import common.socket.CmdMessageGrpIds;
import common.socket.UDP;
import common.struct.CheckInfo;
import common.struct.InvServiceConfig;
import common.struct.SensorData;
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
import java.util.concurrent.CompletableFuture;

public class AppInvServer{
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
//        File traceDir = new File("output/platform/service/inv/" + appConfig.getAppName() + "/trace");
//        traceDir.mkdirs();
//        File invDir = new File("output/platform/service/inv/" + appConfig.getAppName() + "/invDaikon");
//        invDir.mkdirs();
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
//        System.out.println("check");
        CompletableFuture.runAsync(() -> {
            //            System.out.println("here");
//            JSONObject jo = checkChannel.blockTake();
//            System.out.println("here" + jo);
            int lineNumber = jo.getIntValue("line_number");
            long checkTime = jo.getLongValue("check_time");
            int iterId = jo.getIntValue("iter_id");
            JSONObject objs = jo.getJSONObject("objs");

            if (!lines.containsKey(lineNumber)) {
                String[] checkNames = objs.keySet().toArray(new String[0]);
                lines.put(lineNumber, new InvLine(config, checkNames, appConfig.getAppName(), lineNumber));
            }

            InvLine line = lines.get(lineNumber);
            //TODO:获取环境上下文
            List<Double> tmp = new ArrayList<>();
            CmdMessageGrpIds send = new CmdMessageGrpIds("sensory_request", null, List.of(appConfig.getGrpId()));
            for (SensorConfig config : appConfig.getSensors()) {
                String sensorName = config.getSensorName();
                if (!appConfig.getRequestMap().containsKey(sensorName)) {
                    appConfig.getRequestMap().put(sensorName, new SynchronousString());
                }
                appConfig.getRequestMap().get(sensorName).put("invGetSensorData");
//                System.out.println("here" + appConfig.getRequestMap().get(sensorName).size());
                Publisher.publish(sensorName + "_request", send.toString());
//                System.out.println("here" + appConfig.getRequestMap().get(sensorName).size());
                SensorData sensorData = _invGetSensorData.blockTake();
//                System.out.println("here" + sensorData);
                for (Object v : sensorData.getAllData().values()) {
                    //把v转换成double
//                    System.out.println("here " + v);
                    tmp.add(Double.parseDouble(v.toString()));
                }
            }
//            System.out.println("here" + tmp);
            double[] envCtxVals = tmp.stream().mapToDouble(Double::doubleValue).toArray();
//            System.out.println("here" + Arrays.toString(envCtxVals));
//            double[] checkVals = Util.toDoubleArray(objs.values());
            CheckResult result = line.check(new InvData(envCtxVals, JSON.parseObject(objs.toJSONString(),
                    new TypeReference<Map<String, Double>>(){})));

            //TODO:返回checkInfo给appDriver并传递给app
            JSONObject joo = new JSONObject();
            joo.put("sensor_data_type", SensorDataType.INV_REPORT);
            joo.put("name", "INV_REPORT" + lineNumber);
            joo.put("line_number", lineNumber);
            joo.put("iter_id", iterId);
            joo.put("check_time", checkTime);
            joo.put("result", result);
            JSONObject jooo = new JSONObject(2);
            jooo.put("channel", "INV_REPORT" + lineNumber);
            jooo.put("msg", joo);
//            System.out.println("here" + jooo);
            UDP.send(appConfig.getAppDriver().getClientIP(), appConfig.getAppDriver().getClientUDPPort(), jooo.toJSONString());
        });
        return true;
    }
}
