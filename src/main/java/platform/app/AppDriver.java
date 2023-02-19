package platform.app;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import common.socket.UDP;
import common.struct.CtxServiceConfig;
import common.struct.InvServiceConfig;
import common.struct.ServiceConfig;
import common.struct.enumeration.SensorMode;
import platform.Platform;
import platform.communication.pubsub.AbstractSubscriber;
import platform.communication.socket.Cmd;
import platform.communication.socket.PlatformUDP;
import common.socket.TCP;
import platform.config.ActorConfig;
import platform.config.AppConfig;
import platform.config.Configuration;
import common.struct.enumeration.CmdType;
import common.struct.enumeration.ServiceType;
import platform.config.SensorConfig;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AppDriver extends AbstractSubscriber implements Runnable {
    private TCP tcp;
    private String clientIP;
    private int clientUDPPort;
    private int grpId;
    private boolean getMsgThreadState = false;
    private AppConfig appConfig = null;
    private ConcurrentHashMap<String, String> values = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Lock> valueLocks = new ConcurrentHashMap<>();

    public AppDriver(Socket socket) {
        this.tcp = new TCP(socket, false);
    }

    @Override
    public void onMessage(String channel, String msg) {

//        logger.info(String.format("appDriver onmessage: %s, %s", channel, msg));
//        JSONObject jo = JSON.parseObject(msg);
        values.put(channel, msg);
        if (valueLocks.get(channel).tryLock() && getMsgThreadState) {
            //TODO: udp通信触发
            JSONObject jo = new JSONObject(2);
            jo.put("channel", channel);
            jo.put("msg", msg);
            UDP.send(clientIP, clientUDPPort, jo.toJSONString());
        }
        valueLocks.get(channel).unlock();

//        logger.info("onMessage: " + values);
    }

    private static final String CTX_FILE_PATH = "Resources/configFile/ctxFile";

    public void registerSensor(String sensorName, SensorMode mode, int freq) {
        SensorConfig sensorConfig = Configuration.getResourceConfig().getSensorsConfig().get(sensorName);

        if (!appConfig.getSensors().contains(sensorConfig)) {
            appConfig.getSensors().add(sensorConfig);
            sensorConfig.getApps().add(appConfig);
            subscribe(sensorName);
        }

        if (sensorConfig.getTimeLine().size() != 0) {
            sensorConfig.getTimeLineLock().lock();
        }

        if (mode == SensorMode.ACTIVE ||
                (mode == SensorMode.PASSIVE &&
                        sensorConfig.getTimeLine().getAppGrpId2Freq().get(grpId) != freq)) {
            sensorConfig.getTimeLine().getAppGrpId2Freq().remove(grpId);
            sensorConfig.getTimeLine().deleteAppGrpId(grpId, freq);
        } else {
            sensorConfig.getTimeLine().getAppGrpId2Freq().put(grpId, freq);
            sensorConfig.getTimeLine().insertAppGrpId(grpId, freq);
        }

        if (sensorConfig.getTimeLine().size() != 0) {
            sensorConfig.getTimeLineLock().unlock();
        }
    }

    public void cancelSensor(String sensorName) {
        SensorConfig sensorConfig = Configuration.getResourceConfig().getSensorsConfig().get(sensorName);

        if (appConfig.getSensors().contains(sensorConfig)) {
            appConfig.getSensors().remove(sensorConfig);
            sensorConfig.getApps().remove(appConfig);
            if (sensorConfig.getApps().size() == 0) {
                //TODO:可能有死锁
                sensorConfig.stopGetValue();
            }
            unsubscribe(sensorName);
        }

        if (sensorConfig.getTimeLine().size() != 0) {
            sensorConfig.getTimeLineLock().lock();
        }

        if (sensorConfig.getTimeLine().getAppGrpId2Freq().containsKey(grpId)) {
            int freq = sensorConfig.getTimeLine().getAppGrpId2Freq().get(grpId);
            sensorConfig.getTimeLine().getAppGrpId2Freq().remove(grpId);
            sensorConfig.getTimeLine().deleteAppGrpId(grpId, freq);
        }

        if (sensorConfig.getTimeLine().size() != 0) {
            sensorConfig.getTimeLineLock().unlock();
        }
    }

    public String getSensorData(String sensorName) {
        String value = "{\"default\":\"@#$%\"}";
        if (appConfig != null && appConfig.getSensorsName().contains(sensorName)) {
            Cmd cmd = new Cmd("sensor_get", sensorName + " " + grpId);
            PlatformUDP.send(cmd);
            if (!valueLocks.containsKey(sensorName)) {
                valueLocks.put(sensorName, new ReentrantLock(false));
            }
            valueLocks.get(sensorName).lock();
            //等待onMessage触发来解上一行加的锁
            valueLocks.get(sensorName).lock();
            value = values.get(sensorName);
            valueLocks.get(sensorName).unlock();
        }
        return value;
    }

    @Override
    public void run() {
        String msgFromClient;
        Map<String, SensorConfig> sensorConfigMap = Configuration.getResourceConfig().getSensorsConfig();
        Map<String, ActorConfig> actuatorConfigMap = Configuration.getResourceConfig().getActorsConfig();
        while ((msgFromClient = tcp.recv()) != null) {
            // TODO:
            JSONObject jo = JSON.parseObject(msgFromClient);
            logger.info(jo.toJSONString());
            String api = jo.getString("api");

            if (api.equalsIgnoreCase("connect")) {
                clientIP = tcp.getSocket().getInetAddress().getHostAddress();
                clientUDPPort = AppMgrThread.getNewPort(tcp.getSocket());
                grpId = AppMgrThread.getNewGrpId();
                JSONObject retJson = new JSONObject(1);
                retJson.put("state", true);
                tcp.send(retJson.toJSONString());
            } else if (api.equalsIgnoreCase("disconnect")) {
                JSONObject retJson = new JSONObject(1);
                if (appConfig != null) {
                    appConfig.getSensorsName().forEach(sensorName -> {
                        cancelSensor(sensorName);
                    });
                    appConfig.getActorsName().forEach(appConfig::cancelActor);
                    Configuration.getAppsConfig().remove(appConfig.getAppName());
                    appConfig = null;
                    AppMgrThread.removePort(tcp.getSocket(), clientUDPPort);
                    AppMgrThread.removeGrpId(grpId);
                    //TODO:还有剩余资源待释放
                    retJson.put("state", true);
                } else {
                    retJson.put("state", false);
                }
                tcp.send(retJson.toJSONString());
            } else if (api.equalsIgnoreCase("is_connected")) {
                JSONObject retJson = new JSONObject(1);
                if (appConfig != null) {
                    retJson.put("state", true);
                } else {
                    retJson.put("state", false);
                }
                tcp.send(retJson.toJSONString());
            } else if (api.equalsIgnoreCase("register_app")) {
                JSONObject retJson = new JSONObject(2);
                if (appConfig == null) {
                    String appName = jo.getString("app_name");
                    appConfig = new AppConfig(appName);
                    appConfig.setGrpId(grpId);
                    Configuration.getAppsConfig().put(appName, appConfig);
                    retJson.put("state", true);
                    retJson.put("udp_port", clientUDPPort);
                    tcp.send(retJson.toJSONString());
                } else {
                    retJson.put("state", false);
                    tcp.send(retJson.toJSONString());
                }
            } else if (api.equalsIgnoreCase("unregister_app")) {
                JSONObject retJson = new JSONObject(1);
                if (appConfig != null && appConfig.getAppName().equalsIgnoreCase(jo.getString("app_name"))) {
                    appConfig.getSensorsName().forEach(sensorName -> {
                        cancelSensor(sensorName);
                    });
                    appConfig.getActorsName().forEach(appConfig::cancelActor);
                    Configuration.getAppsConfig().remove(appConfig.getAppName());
                    appConfig = null;
                    AppMgrThread.removePort(tcp.getSocket(), clientUDPPort);
                    AppMgrThread.removeGrpId(grpId);
                    //TODO:还有剩余资源待释放
                    retJson.put("state", true);
                } else {
                    retJson.put("state", false);
                }
                tcp.send(retJson.toJSONString());
//
//                logger.info("\n" + appName + " exit!");
//                logger.info("sensors:");
//                sensorConfigMap.forEach((s, config) -> {
//                    logger.info(s + " -> " + config.getAppsName());
//                });
//                logger.info("actuators:");
//                actuatorConfigMap.forEach((s, config) -> {
//                    logger.info(s + " -> " + config.getAppsName());
//                });
//                logger.info("appGrpIds:");
//                appConfigMap.forEach((s, config) -> {
//                    logger.info(s + " -> sensors:" + config.getSensorsName() + ", actuators:" + config.getActorsName());
//                });
            } else if (api.equalsIgnoreCase("get_supported_sensors")) {
                JSONArray retJsonArray = new JSONArray();
                sensorConfigMap.forEach((sensorName, config) -> {
                    JSONObject joo = new JSONObject(3);
                    joo.put("sensor_name", sensorName);
                    joo.put("state", config.isAlive() ? "on" : "off");
                    joo.put("value_type", config.getSensorType());
                    retJsonArray.add(joo);
                });
                tcp.send(retJsonArray.toJSONString());
            } else if (api.equalsIgnoreCase("get_registered_sensors")) {
                JSONArray retJsonArray = new JSONArray();
                if (appConfig != null) {
                    appConfig.getSensors().forEach(config -> {
                        JSONObject joo = new JSONObject(3);
                        joo.put("sensor_name", config.getSensorName());
                        joo.put("state", config.isAlive() ? "on" : "off");
                        joo.put("value_type", config.getSensorType());
                        retJsonArray.add(joo);
                    });
                }
                tcp.send(retJsonArray.toJSONString());
            } else if (api.equalsIgnoreCase("get_registered_sensors_status")) {
                JSONObject retJson = new JSONObject(1);
                boolean status = true;
                if (appConfig != null) {
                    for (SensorConfig config : appConfig.getSensors()) {
                        if (!config.isAlive()) {
                            status = false;
                            break;
                        }
                    }
                } else {
                    status = false;
                }
                retJson.put("state", status);
                tcp.send(retJson.toJSONString());
            } else if (api.equalsIgnoreCase("register_sensor")) {
                JSONObject retJson = new JSONObject(1);
                String sensorName = jo.getString("sensor_name");
                SensorMode mode = SensorMode.fromString(jo.getString("mode"));
                int freq = jo.getIntValue("freq");
                SensorConfig sensorConfig = sensorConfigMap.get(sensorName);
                if (appConfig != null
                        && sensorConfigMap.containsKey(sensorName)
                        && ((mode == SensorMode.ACTIVE && freq == -1) ||
                            (mode == SensorMode.PASSIVE && sensorConfig.checkValueFreq(freq)))) {
                    registerSensor(sensorName, mode, freq);
                    retJson.put("state", true);
                } else {
                    retJson.put("state", false);
                }
                tcp.send(retJson.toJSONString());
            } else if (api.equalsIgnoreCase("cancel_sensor")) {
                JSONObject retJson = new JSONObject(1);
                String sensorName = jo.getString("sensor_name");
                if (appConfig != null && appConfig.getSensorsName().contains(sensorName)) {
                    cancelSensor(sensorName);
                    retJson.put("state", true);
                } else {
                    retJson.put("state", false);
                }
                tcp.send(retJson.toJSONString());
            } else if (api.equalsIgnoreCase("cancel_all_sensors")) {
                JSONObject retJson = new JSONObject(1);
                appConfig.getSensorsName().forEach(this::cancelSensor);
                retJson.put("state", true);
                tcp.send(retJson.toJSONString());
            } else if (api.equalsIgnoreCase("get_sensor_data")) {
                String sensorName = jo.getString("sensor_name");
                String value = getSensorData(sensorName);
                tcp.send(value);
            } else if (api.equalsIgnoreCase("get_all_sensor_data")) {
                JSONArray ja = new JSONArray();
                appConfig.getSensorsName().forEach(sensorName -> {
                    JSONObject joo = new JSONObject(2);
                    joo.put("sensor_name", sensorName);
                    joo.put("value", getSensorData(sensorName));
                    ja.add(joo);
                });
                tcp.send(ja.toJSONString());
            } else if (api.equalsIgnoreCase("get_msg_thread")) {
                CmdType cmd = CmdType.fromString(jo.getString("cmd"));
                JSONObject retJson = new JSONObject(1);
                if (cmd == CmdType.START) {
                    getMsgThreadState = true;
                } else if (cmd == CmdType.STOP) {
                    getMsgThreadState = false;
                }
                retJson.put("state", true);
                tcp.send(retJson.toJSONString());
            }else if (api.equalsIgnoreCase("get_supported_actuators")) {
                JSONArray retJsonArray = new JSONArray();
                actuatorConfigMap.forEach((actuatorName, config) -> {
                    JSONObject joo = new JSONObject(3);
                    joo.put("actuator_name", actuatorName);
                    joo.put("state", config.isAlive() ? "on" : "off");
                    joo.put("value_type", config.getActorType());
                    retJsonArray.add(joo);
                });
                tcp.send(retJsonArray.toJSONString());
            } else if (api.equalsIgnoreCase("get_registered_actuators")) {
                JSONArray retJsonArray = new JSONArray();
                if (appConfig != null) {
                    appConfig.getActors().forEach(config -> {
                        JSONObject joo = new JSONObject(3);
                        joo.put("actuator_name", config.getActorName());
                        joo.put("state", config.isAlive() ? "on" : "off");
                        joo.put("value_type", config.getActorType());
                        retJsonArray.add(joo);
                    });
                }

                tcp.send(retJsonArray.toJSONString());
            } else if (api.equalsIgnoreCase("get_registered_actuators_status")) {
                JSONObject retJson = new JSONObject(1);
                boolean state = true;
                if (appConfig != null) {
                    for (ActorConfig config : appConfig.getActors()) {
                        if (!config.isAlive()) {
                            state = false;
                            break;
                        }
                    }
                } else {
                    state = false;
                }

                retJson.put("state", state);
                tcp.send(retJson.toJSONString());
            } else if (api.equalsIgnoreCase("register_actuator")) {
                JSONObject retJson = new JSONObject(1);
                String actuatorName = jo.getString("actuator_name");
                if (appConfig != null && actuatorConfigMap.containsKey(actuatorName)) {
                    ActorConfig actorConfig = actuatorConfigMap.get(actuatorName);
                    appConfig.getActors().add(actorConfig);
                    actorConfig.getApps().add(appConfig);
                    retJson.put("state", true);
                } else {
                    retJson.put("state", false);
                }
                tcp.send(retJson.toJSONString());
            } else if (api.equalsIgnoreCase("cancel_actuator")) {
                JSONObject retJson = new JSONObject(1);
                String actuatorName = jo.getString("actuator_name");
                if (appConfig != null && appConfig.getActorsName().contains(actuatorName)) {
                    ActorConfig actorConfig = actuatorConfigMap.get(actuatorName);
                    appConfig.getActors().remove(actorConfig);
                    actorConfig.getApps().remove(appConfig);
                    retJson.put("state", true);
                } else {
                    retJson.put("state", false);
                }
                tcp.send(retJson.toJSONString());
            } else if (api.equalsIgnoreCase("cancel_all_actuators")) {
                JSONObject retJson = new JSONObject(1);
                if (appConfig != null) {
                    appConfig.getActors().forEach(actorConfig -> {
                        actorConfig.getApps().remove(appConfig);
                    });
                    appConfig.getActors().clear();
                    retJson.put("state", true);
                } else {
                    retJson.put("state", false);
                }
                tcp.send(retJson.toJSONString());
            } else if (api.equalsIgnoreCase("set_actuator")) {
                JSONObject retJson = new JSONObject(1);
                String actuatorName = jo.getString("actuator_name");
                //TODO:actorType
                String cmd = jo.getString("cmd_type");
                JSONArray argsJsonArray = jo.getJSONArray("args");
                List<String> args = new ArrayList<>();
                args.add(cmd);
                for (Object joo : argsJsonArray) {
                    JSONObject arg = (JSONObject) joo;
                    args.add(arg.getString("arg"));
                }
                if (appConfig != null
                        && appConfig.getActorsName().contains(actuatorName)
                        && actuatorConfigMap.get(actuatorName).isAlive()) {
                    PlatformUDP.send(new Cmd("actuator_set", String.join(" ", args)));
                    retJson.put("state", true);
                } else {
                    retJson.put("state", false);
                }
                tcp.send(retJson.toJSONString());
            } else if (api.equalsIgnoreCase("is_service_on")) {
                //TODO:是appCtxServer还是PlatformCtxServer
            } else if (api.equalsIgnoreCase("start_service")) {
                JSONObject retJson = new JSONObject(1);
                ServiceType service = ServiceType.fromString(jo.getString("service_type"));
                ServiceConfig config;
                if (service == ServiceType.CTX) {
                    config = CtxServiceConfig.fromJSONString(jo.getString("config"));
                } else {
                    config = InvServiceConfig.fromJSONString(jo.getString("config"));
                }
                boolean ret = Platform.call(appConfig.getAppName(), service, CmdType.START, config);
                retJson.put("ret", ret);
                tcp.send(retJson.toJSONString());
            } else if (api.equalsIgnoreCase("stop_service")) {
                JSONObject retJson = new JSONObject(1);
                ServiceType service = ServiceType.fromString(jo.getString("service_type"));
                boolean ret = Platform.call(appConfig.getAppName(), service, CmdType.STOP, null);
                retJson.put("ret", ret);
                tcp.send(retJson.toJSONString());
            } else if (api.equalsIgnoreCase("service_call")) {
                JSONObject retJson = new JSONObject(1);
                ServiceType service = ServiceType.fromString(jo.getString("service_type"));
                CmdType cmd = CmdType.fromString(jo.getString("cmd_type"));
                ServiceConfig config;
                if (service == ServiceType.CTX) {
                    config = CtxServiceConfig.fromJSONString(jo.getString("config"));
                } else {
                    config = InvServiceConfig.fromJSONString(jo.getString("config"));
                }
                boolean ret = Platform.call(appConfig.getAppName(), service, cmd, config);
                retJson.put("ret", ret);
                tcp.send(retJson.toJSONString());
            }
//            else if (api.equalsIgnoreCase("set_rule_file")) {
//                JSONObject retJson = new JSONObject(1);
//                String content = jo.getString("content");
//                String dir = CTX_FILE_PATH + "/" + appName;
//                Util.writeFileContent(dir, jo.getString("file_name"), content);
//                if (appConfig != null) {
//                    appConfig.setRuleFile(dir + "/" + jo.getString("file_name"));
//                    retJson.put("state", true);
//                } else {
//                    retJson.put("state", false);
//                }
//                tcp.send(retJson.toJSONString());
//            } else if (api.equalsIgnoreCase("set_pattern_file")) {
//                JSONObject retJson = new JSONObject(1);
//                String content = jo.getString("content");
//                String dir = CTX_FILE_PATH + "/" + appName;
//                Util.writeFileContent(dir, jo.getString("file_name"), content);
//                if (appConfig != null) {
//                    appConfig.setPatternFile(dir + "/" + jo.getString("file_name"));
//                    retJson.put("state", true);
//                } else {
//                    retJson.put("state", false);
//                }
//                tcp.send(retJson.toJSONString());
//            } else if (api.equalsIgnoreCase("set_bfunc_file")) {
//                JSONObject retJson = new JSONObject(1);
//                String content = jo.getString("content");
//                String dir = CTX_FILE_PATH + "/" + appName;
//                String fileName = jo.getString("file_name");
//                String javaFile = fileName.replace(".class", ".java");
//                Util.writeFileContent(dir, javaFile, content);
//                String[] args = new String[] {dir + "/" + javaFile};
//                if(Main.compile(args) == 0 && appConfig != null) {
//                    appConfig.setBfuncFile(dir + "/" + fileName);
//                    retJson.put("state", true);
//                } else {
//                    retJson.put("state", false);
//                }
//                tcp.send(retJson.toJSONString());
//            } else if (api.equalsIgnoreCase("set_mfunc_file")) {
//                JSONObject retJson = new JSONObject(1);
//                String content = jo.getString("content");
//                String dir = CTX_FILE_PATH + "/" + appName;
//                String fileName = jo.getString("file_name");
//                String javaFile = fileName.replace(".class", ".java");
//                Util.writeFileContent(dir, javaFile, content);
//                String[] args = new String[] {dir + "/" + javaFile};
//                if(Main.compile(args) == 0 && appConfig != null) {
//                    appConfig.setMfuncFile(dir + "/" + fileName);
//                    retJson.put("state", true);
//                } else {
//                    retJson.put("state", false);
//                }
//                tcp.send(retJson.toJSONString());
//            } else if (api.equalsIgnoreCase("set_rfunc_file")) {
//                //TODO:ctx还未实现该功能
//            } else if (api.equalsIgnoreCase("set_ctx_validator")) {
//                JSONObject retJson = new JSONObject(1);
//                String ctxValidator = jo.getString("ctx_validator");
//                if (appConfig != null) {
//                    appConfig.setCtxValidator(ctxValidator);
//                    retJson.put("state", true);
//                } else {
//                    retJson.put("state", false);
//                }
//                tcp.send(retJson.toJSONString());
//            }
        }
        tcp.close();
    }
}
