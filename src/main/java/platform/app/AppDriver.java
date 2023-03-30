package platform.app;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import common.socket.AbstractTCP;
import common.socket.CmdMessageGrpIds;
import common.socket.TCP;
import common.socket.UDP;
import common.struct.*;
import common.struct.enumeration.SensorMode;
import common.struct.sync.SynchronousSensorData;
import common.struct.sync.SynchronousString;
import platform.Platform;
import platform.communication.pubsub.AbstractSubscriber;
import platform.communication.pubsub.Publisher;
import platform.communication.socket.Cmd;
import platform.communication.socket.PlatformUDP;
import platform.config.ActorConfig;
import platform.config.AppConfig;
import platform.config.Configuration;
import common.struct.enumeration.CmdType;
import common.struct.enumeration.ServiceType;
import platform.config.SensorConfig;
import platform.service.inv.AppInvServer;
import platform.service.inv.PlatformInvServer;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class AppDriver extends AbstractSubscriber implements Runnable {
    private TCP tcp;
    private String clientIP = null;
    private int clientUDPPort = -1;
    private int grpId = -1;
    private boolean getMsgThreadState = false;
    private AppConfig appConfig = null;
    private final SynchronousString _getSensorData = new SynchronousString();
    private final SynchronousSensorData _invGetSensorData = new SynchronousSensorData();
    private final SynchronousString _setActorCmd = new SynchronousString();

    public AppDriver(Socket socket) {
        this.tcp = new AppDriverTCP(socket, false);
    }

    public class AppDriverTCP extends AbstractTCP {
        public AppDriverTCP(Socket socket, boolean lockFlag) {
            super(socket, lockFlag);
        }

        public AppDriverTCP(Socket socket) {
            super(socket);
        }

        @Override
        public void close() {
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            disConnectPlatform();
        }

        @Override
        public void callBack() {
            String name = "AppDriver";
            if (appConfig != null) {
                name = appConfig.getAppName();
            }
            logger.error(String.format("[%s]: TCP connection is broken. Start releasing app resources...", name));
            if (disConnectPlatform().equals("{\"state\":true}")) {
                logger.info(String.format("[%s]: App resource released successfully.", name));
            } else {
                logger.error(String.format("[%s]: App resource released failed.", name));
            }
        }
    }

    @Override
    public void onMessage(String channel, String msg) {
//        logger.info(String.format("appDriver onmessage: %s, %s", channel, msg));
        String flag = appConfig.getRequestMap().get(channel).nonBlockTake();
//        logger.info(flag);
        if (flag.equalsIgnoreCase("getSensorData")) {
            _getSensorData.put(msg);
        } else if (flag.equalsIgnoreCase("invGetSensorData")) {
            _invGetSensorData.put(SensorData.fromJSONString(msg));
        } else if (flag.equalsIgnoreCase("passiveGetSensorData")) {
            JSONObject jo = new JSONObject(2);
            jo.put("channel", channel);
            jo.put("msg", msg);
            UDP.send(clientIP, clientUDPPort, jo.toJSONString());
        } else if (flag.equalsIgnoreCase("setActorCmd")) {
            _setActorCmd.put(msg);
        }


//        logger.info(String.format("appDriver onmessage: %s, %s", channel, msg));
//        if (appConfig.getSensorsName().contains(channel)) {
//            if (!getSensorDataFlag.containsKey(channel)) {
//                getSensorDataFlag.put(channel, false);
//            }
//            if (getMsgThreadState && !getSensorDataFlag.get(channel)) {
//                //TODO: udp通信触发
//
//            } else {
//                //getSensorData
//                if (!sensorValues.containsKey(channel)) {
//                    sensorValues.put(channel, new SynchronousSensorData(1));
//                }
//                sensorValues.get(channel).put(SensorData.fromJSONString(msg));
//                getSensorDataFlag.put(channel, false);
//            }
//        } else {
//            //actorSetCmd
//            if (!actorSetState.containsKey(channel)) {
//                actorSetState.put(channel, new SynchronousSetState(1));
//            }
//            actorSetState.get(channel).put(SetState.fromString(msg));
//        }
    }

    private static final String CTX_FILE_PATH = "Resources/configFile/ctxFile";

    @Override
    public void run() {
        String msgFromClient;
        while ((msgFromClient = tcp.recv()) != null) {
            JSONObject jo = JSON.parseObject(msgFromClient);
            if (appConfig != null) {
                logger.info(String.format("[%s -> Platform]: %s", appConfig.getAppName(), jo.toJSONString()));
            } else {
                logger.info(String.format("[AppDriver -> Platform]: %s", jo.toJSONString()));
            }

            String api = jo.getString("api");
            String ret = null;

            if (api.equalsIgnoreCase("connect")) {
                ret = connectPlatform();
            } else if (api.equalsIgnoreCase("disconnect")) {
                ret = disConnectPlatform();
            } else if (api.equalsIgnoreCase("is_connected")) {
                ret = checkConnect();
            } else if (api.equalsIgnoreCase("register_app")) {
                String appName = jo.getString("app_name");
                ret = registerApp(appName);
            } else if (api.equalsIgnoreCase("unregister_app")) {
                String appName = jo.getString("app_name");
                ret = unregisterApp(appName);
            } else if (api.equalsIgnoreCase("get_supported_sensors")) {
                ret = getSupportedSensors();
            } else if (api.equalsIgnoreCase("get_registered_sensors")) {
                ret = getRegisteredSensors();
            } else if (api.equalsIgnoreCase("get_registered_sensors_status")) {
                ret = getRegisteredSensorsStatus();
            } else if (api.equalsIgnoreCase("register_sensor")) {
                String sensorName = jo.getString("sensor_name");
                SensorMode mode = SensorMode.fromString(jo.getString("mode"));
                int freq = jo.getIntValue("freq");
                ret = registerSensor(sensorName, mode, freq);
            } else if (api.equalsIgnoreCase("cancel_sensor")) {
                String sensorName = jo.getString("sensor_name");
                ret = cancelSensor(sensorName);
            } else if (api.equalsIgnoreCase("cancel_all_sensors")) {
                ret = cancelAllSensors();
            } else if (api.equalsIgnoreCase("get_sensor_data")) {
                String sensorName = jo.getString("sensor_name");
                ret = getSensorData(sensorName);
            } else if (api.equalsIgnoreCase("get_all_sensor_data")) {
                ret = getAllSensorData();
            } else if (api.equalsIgnoreCase("get_msg_thread")) {
                CmdType cmd = CmdType.fromString(jo.getString("cmd"));
                ret = getMsgThread(cmd);
            }else if (api.equalsIgnoreCase("get_supported_actors")) {
                ret = getSupportedActors();
            } else if (api.equalsIgnoreCase("get_registered_actors")) {
                ret = getRegisteredActors();
            } else if (api.equalsIgnoreCase("get_registered_actors_status")) {
                ret = getRegisteredActorsStatus();
            } else if (api.equalsIgnoreCase("register_actor")) {
                String actorName = jo.getString("actor_name");
                ret =  registerActor(actorName);
            } else if (api.equalsIgnoreCase("cancel_actor")) {
                String actorName = jo.getString("actor_name");
                ret = cancelActor(actorName);
            } else if (api.equalsIgnoreCase("cancel_all_actors")) {
                ret = cancelAllActors();
            } else if (api.equalsIgnoreCase("set_actor_cmd")) {
                String actorName = jo.getString("actor_name");
                String action = jo.getString("action");
                ret = setActorCmd(actorName, action);
            } else if (api.equalsIgnoreCase("is_service_on")) {
                ServiceType service = ServiceType.fromString(jo.getString("service_type"));
                ret = isServiceOn(service);
            } else if (api.equalsIgnoreCase("start_service")) {
                ServiceType service = ServiceType.fromString(jo.getString("service_type"));
                ServiceConfig config;
                if (service == ServiceType.CTX) {
                    config = CtxServiceConfig.fromJSONString(jo.getString("config"));
                } else {
                    config = InvServiceConfig.fromJSONString(jo.getString("config"));
                }
                ret = serviceCall(service, CmdType.START, config);
            } else if (api.equalsIgnoreCase("stop_service")) {
                ServiceType service = ServiceType.fromString(jo.getString("service_type"));
                ret = serviceCall(service, CmdType.STOP, null);
            } else if (api.equalsIgnoreCase("service_call")) {
                ServiceType service = ServiceType.fromString(jo.getString("service_type"));
                CmdType cmd = CmdType.fromString(jo.getString("cmd_type"));
                ServiceConfig config;
                if (service == ServiceType.CTX) {
                    config = CtxServiceConfig.fromJSONString(jo.getString("config"));
                } else {
                    config = InvServiceConfig.fromJSONString(jo.getString("config"));
                }
                ret = serviceCall(service, cmd, config);
            } else if (api.equalsIgnoreCase("inv_monitor")) {
                ret = monitor(jo.getJSONArray("objs"));
            } else if (api.equalsIgnoreCase("inv_is_monitored")) {
                ret = isMonitored(jo.getJSONArray("objs"));
            } else if (api.equalsIgnoreCase("inv_check")) {
                ret = check(jo);
            }
            tcp.send(ret);
            if (appConfig != null) {
                logger.info(String.format("[Platform -> %s]: %s", appConfig.getAppName(), ret));
            } else {
                logger.info(String.format("[Platform -> AppDriver]: %s", ret));
            }
            if (api.equalsIgnoreCase("disconnect")) {
                break;
            }
        }
        tcp.close();
    }

    private String connectPlatform() {
//        grpId = AppMgrThread.getNewGrpId();
        JSONObject retJson = new JSONObject(1);
        retJson.put("state", true);
//        logger.info("clientIP: " + clientIP + ", clientUDPPort: " + clientUDPPort + ", grpId: " + grpId);
        return retJson.toJSONString();
    }

    private String disConnectPlatform() {
        String ret = "{\"state\":true}";
        if (appConfig != null) {
            ret = unregisterApp(appConfig.getAppName());
        }
//        AppMgrThread.removeGrpId(grpId);
//        grpId = -1;
        return ret;
    }

    private String checkConnect() {
        JSONObject retJson = new JSONObject(1);
        if (appConfig != null) {
            retJson.put("state", true);
        } else {
            retJson.put("state", false);
        }
        return retJson.toJSONString();
    }

    private String registerApp(String appName) {
        JSONObject retJson = new JSONObject(2);
        if (appConfig == null) {
            clientIP = tcp.getSocket().getInetAddress().getHostAddress();
            clientUDPPort = AppMgrThread.getNewPort(tcp.getSocket());
            grpId = AppMgrThread.getNewGrpId(appName);
            appConfig = new AppConfig(appName);
            appConfig.setGrpId(grpId);
            Configuration.getAppsConfig().put(appName, appConfig);
            retJson.put("state", true);
            retJson.put("udp_port", clientUDPPort);
        } else {
            retJson.put("state", false);
        }
        return retJson.toJSONString();
    }

    private String unregisterApp(String appName) {
        JSONObject retJson = new JSONObject(1);
        if (appConfig != null && appConfig.getAppName().equalsIgnoreCase(appName)) {
            cancelAllSensors();
            cancelAllActors();
            Configuration.getAppsConfig().remove(appName);
            appConfig = null;
            AppMgrThread.removePort(tcp.getSocket(), clientUDPPort);
            clientIP = null;
            clientUDPPort = -1;
            getMsgThreadState = false;
            //TODO:还有剩余资源待释放
            serviceCall(ServiceType.ALL, CmdType.STOP, null);
            retJson.put("state", true);
        } else {
            retJson.put("state", false);
        }
//        logger.info("\n" + appName + " exit!");
//        logger.info("sensors:");
//        sensorConfigMap.forEach((s, config) -> {
//            logger.info(s + " -> " + config.getAppsName());
//        });
//        logger.info("actors:");
//        actorConfigMap.forEach((s, config) -> {
//            logger.info(s + " -> " + config.getAppsName());
//        });
//        logger.info("appNames:");
//        appConfigMap.forEach((s, config) -> {
//            logger.info(s + " -> sensors:" + config.getSensorsName() + ", actors:" + config.getActorsName());
//        });
        return retJson.toJSONString();
    }

    private String getSupportedSensors() {
        JSONArray retJsonArray = new JSONArray();
        Configuration.getResourceConfig().getSensorsConfig().forEach((sensorName, config) -> {
            JSONObject joo = new JSONObject(3);
            joo.put("sensor_name", sensorName);
            joo.put("state", config.isAlive() ? State.ON : State.OFF);
            joo.put("value_type", config.getSensorType());
            retJsonArray.add(joo);
        });
        return retJsonArray.toJSONString();
    }

    private String getRegisteredSensors() {
        JSONArray retJsonArray = new JSONArray();
        if (appConfig != null) {
            appConfig.getSensors().forEach(config -> {
                JSONObject joo = new JSONObject(3);
                joo.put("sensor_name", config.getSensorName());
                joo.put("state", config.isAlive() ? State.ON : State.OFF);
                joo.put("value_type", config.getSensorType());
                retJsonArray.add(joo);
            });
        }
        return retJsonArray.toJSONString();
    }

    private String getRegisteredSensorsStatus() {
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
        return retJson.toJSONString();
    }

    private void _registerSensor(String sensorName, SensorMode mode, int freq) {
        SensorConfig sensorConfig = Configuration.getResourceConfig().getSensorsConfig().get(sensorName);

        if(appConfig.addSensor(sensorConfig)){
            sensorConfig.getApps().add(appConfig);
            subscribe(sensorName, grpId);
        }

//        if (sensorConfig.getTimeLine().size() != 0) {
//            sensorConfig.getTimeLineLock().lock();
//        }
        synchronized (sensorConfig.getTimeLine()) {
            if (mode == SensorMode.ACTIVE) {
                sensorConfig.getTimeLine().deleteAppName(appConfig.getAppName(), freq);
            } else {
                sensorConfig.getTimeLine().insertAppName(appConfig.getAppName(), freq);
            }
            if (sensorConfig.getTimeLine().size() != 0) {
                sensorConfig.getTimeLine().notify();
//                logger.info(sensorConfig.getTimeLine());
            }
        }

//        if (sensorConfig.getTimeLine().size() != 0) {
//            sensorConfig.getTimeLineLock().unlock();
//        }
    }

    private String registerSensor(String sensorName, SensorMode mode, int freq) {
        JSONObject retJson = new JSONObject(1);
        Map<String, SensorConfig> sensorConfigMap = Configuration.getResourceConfig().getSensorsConfig();
        SensorConfig sensorConfig = sensorConfigMap.get(sensorName);
        if (appConfig != null
                && sensorConfigMap.containsKey(sensorName)
                && ((mode == SensorMode.ACTIVE && freq == -1) ||
                (mode == SensorMode.PASSIVE && sensorConfig.checkValueFreq(freq)))) {
            _registerSensor(sensorName, mode, freq);
            retJson.put("state", true);
        } else {
            retJson.put("state", false);
        }
        return retJson.toJSONString();
    }

    public void _cancelSensor(String sensorName) {
        SensorConfig sensorConfig = Configuration.getResourceConfig().getSensorsConfig().get(sensorName);

        if(appConfig.removeSensor(sensorConfig)){
            sensorConfig.getApps().remove(appConfig);
            if (sensorConfig.getApps().size() == 0) {
                //TODO:可能有死锁
                sensorConfig.stopGetValue();
            }
            unsubscribe(sensorName);
        }

//        if (sensorConfig.getTimeLine().size() != 0) {
//            sensorConfig.getTimeLineLock().lock();
//        }
        synchronized (sensorConfig.getTimeLine()) {
            if (sensorConfig.getTimeLine().getAppName2Freq().containsKey(appConfig.getAppName())) {
                int freq = sensorConfig.getTimeLine().getAppName2Freq().get(appConfig.getAppName());
                sensorConfig.getTimeLine().deleteAppName(appConfig.getAppName(), freq);
                sensorConfig.getTimeLine().getAppName2Freq().remove(appConfig.getAppName());
            }
//            if (sensorConfig.getTimeLine().size() != 0) {
//                logger.info(sensorConfig.getTimeLine());
//            }
        }

//        if (sensorConfig.getTimeLine().size() != 0) {
//            sensorConfig.getTimeLineLock().unlock();
//        }
    }

    private String cancelSensor(String sensorName) {
        JSONObject retJson = new JSONObject(1);
        if (appConfig != null && appConfig.getSensorsName().contains(sensorName)) {
            _cancelSensor(sensorName);
            retJson.put("state", true);
        } else {
            retJson.put("state", false);
        }
        return retJson.toJSONString();
    }

    private String cancelAllSensors() {
        JSONObject retJson = new JSONObject(1);
        appConfig.getSensorsName().forEach(this::cancelSensor);
        retJson.put("state", true);
        return retJson.toJSONString();
    }

    private String getSensorData(String sensorName) {
        String value = "{\"default\":\"@#$%\"}";
        if (appConfig != null && appConfig.getSensorsName().contains(sensorName)) {
//            Cmd cmd = new Cmd("sensor_get", sensorName + " " + grpId);
//            PlatformUDP.send(cmd);
//            getSensorDataFlag.put(sensorName, true);
//            if (!sensorValues.containsKey(sensorName)) {
//                sensorValues.put(sensorName, new SynchronousSensorData(1));
//            }
//            value = sensorValues.get(sensorName).blockTake().toString();
            CmdMessageGrpIds send = new CmdMessageGrpIds("sensory_request", null, List.of(grpId));
            if (!appConfig.getRequestMap().containsKey(sensorName)) {
                appConfig.getRequestMap().put(sensorName, new SynchronousString());
            }
            appConfig.getRequestMap().get(sensorName).put("getSensorData");
            Publisher.publish(sensorName + "_request", send.toString());
            value = _getSensorData.blockTake();
        }
        return value;
    }

    private String getAllSensorData() {
        JSONArray ja = new JSONArray();
        appConfig.getSensorsName().forEach(sensorName -> {
            JSONObject joo = new JSONObject(2);
            joo.put("sensor_name", sensorName);
            joo.put("value", getSensorData(sensorName));
            ja.add(joo);
        });
        return ja.toJSONString();
    }

    private String getMsgThread(CmdType cmd) {
        JSONObject retJson = new JSONObject(1);
        if (cmd == CmdType.START) {
            getMsgThreadState = true;
            appConfig.getSensors().forEach(sensorConfig -> {
                if (!sensorConfig.isGetValueRunning()) {
                    sensorConfig.startGetValue();
                }
            });
        } else if (cmd == CmdType.STOP) {
            getMsgThreadState = false;
            appConfig.getSensors().forEach(sensorConfig -> {
                if (sensorConfig.isGetValueRunning() && sensorConfig.getApps().size() == 1 && sensorConfig.getApps().contains(appConfig)) {
                    sensorConfig.stopGetValue();
                }
            });
        }
        retJson.put("state", true);
        return retJson.toJSONString();
    }
    
    private String getSupportedActors() {
        JSONArray retJsonArray = new JSONArray();
        Configuration.getResourceConfig().getActorsConfig().forEach((actorName, config) -> {
            JSONObject joo = new JSONObject(3);
            joo.put("actor_name", actorName);
            joo.put("state", config.isAlive() ? State.ON : State.OFF);
            joo.put("value_type", config.getActorType());
            retJsonArray.add(joo);
        });
        return retJsonArray.toJSONString();
    }

    private String getRegisteredActors() {
        JSONArray retJsonArray = new JSONArray();
        if (appConfig != null) {
            appConfig.getActors().forEach(config -> {
                JSONObject joo = new JSONObject(3);
                joo.put("actor_name", config.getActorName());
                joo.put("state", config.isAlive() ? State.ON : State.OFF);
                joo.put("value_type", config.getActorType());
                retJsonArray.add(joo);
            });
        }
        return retJsonArray.toJSONString();
    }

    private String getRegisteredActorsStatus() {
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
        return retJson.toJSONString();
    }

    private String registerActor(String actorName) {
        JSONObject retJson = new JSONObject(1);
        Map<String, ActorConfig> actorConfigMap = Configuration.getResourceConfig().getActorsConfig();
        if (appConfig != null && actorConfigMap.containsKey(actorName)) {
            ActorConfig actorConfig = actorConfigMap.get(actorName);
            appConfig.getActors().add(actorConfig);
            actorConfig.getApps().add(appConfig);
            subscribe(actorName, grpId);
            retJson.put("state", true);
        } else {
            retJson.put("state", false);
        }
        return retJson.toJSONString();
    }

    private String cancelActor(String actorName) {
        JSONObject retJson = new JSONObject(1);
        Map<String, ActorConfig> actorConfigMap = Configuration.getResourceConfig().getActorsConfig();
        if (appConfig != null && appConfig.getActorsName().contains(actorName)) {
            ActorConfig actorConfig = actorConfigMap.get(actorName);
            appConfig.getActors().remove(actorConfig);
            actorConfig.getApps().remove(appConfig);
            unsubscribe(actorName);
            retJson.put("state", true);
        } else {
            retJson.put("state", false);
        }
        return retJson.toJSONString();
    }

    private String cancelAllActors() {
        JSONObject retJson = new JSONObject(1);
        appConfig.getActorsName().forEach(this::cancelActor);
        retJson.put("state", true);
        return retJson.toJSONString();
    }

//    private String setActorCmd(String actorName, String... args) {
//        JSONObject retJson = new JSONObject(1);
//        Map<String, ActorConfig> actorConfigMap = Configuration.getResourceConfig().getActorsConfig();
//        if (appConfig != null
//                && appConfig.getActorsName().contains(actorName)
//                && actorConfigMap.get(actorName).isAlive()) {
//            PlatformUDP.send(new Cmd("actor_set", String.join(" ", args)));
//            retJson.put("state", true);
//        } else {
//            retJson.put("state", false);
//        }
//        return retJson.toJSONString();
//    }

    private String setActorCmd(String actorName, String action) {
        JSONObject retJson = new JSONObject(1);
        Map<String, ActorConfig> actorConfigMap = Configuration.getResourceConfig().getActorsConfig();
        if (appConfig != null
                && appConfig.getActorsName().contains(actorName)
                && actorConfigMap.get(actorName).isAlive()) {
//            PlatformUDP.send(new Cmd("actor_set", actorName + " " + grpId + " " + action));
//            boolean state = actorSetState.get(actorName).blockTake().get();
            CmdMessageGrpIds send = new CmdMessageGrpIds("action_request", null, List.of(grpId));
            if (!appConfig.getRequestMap().containsKey(actorName)) {
                appConfig.getRequestMap().put(actorName, new SynchronousString());
            }
            appConfig.getRequestMap().get(actorName).put("setActorCmd");
            Publisher.publish(actorName + "_request", send.toString());
            retJson.put("state", true);
        } else {
            retJson.put("state", false);
        }
        return retJson.toJSONString();
    }

    private String isServiceOn(ServiceType service) {
        boolean state = false;
        if (appConfig != null) {
            switch (service) {
                case CTX:
                    state = appConfig.isCtxServerOn();
                    break;
                case INV:
                    state = appConfig.isInvServerOn();
                    break;
                case ALL:
                    state = appConfig.isCtxServerOn() && appConfig.isInvServerOn();
                    break;
            }
        }
        return "{\"state\":" + state + "}";
    }

    private String serviceCall(ServiceType service, CmdType cmd, ServiceConfig config) {
        JSONObject retJson = new JSONObject(1);
        if (appConfig == null) {
            retJson.put("state", false);
        } else {
            boolean ret = Platform.call(appConfig.getAppName(), service, cmd, config);
            retJson.put("state", ret);
        }
        return retJson.toJSONString();
    }

    private String monitor(JSONArray objs) {
        AppInvServer appInvServer = PlatformInvServer.getAppInvServer(appConfig.getAppName());
        boolean ret = false;
        if (appInvServer != null) {
            ret = appInvServer.monitor(objs.toJavaList(String.class));
        }
        return "{\"state\":" + ret + "}";
    }

    private String isMonitored(JSONArray objs) {
        AppInvServer appInvServer = PlatformInvServer.getAppInvServer(appConfig.getAppName());
        boolean ret = false;
        if (appInvServer != null) {
            ret = appInvServer.isMonitored(objs.toJavaList(String.class));
        }
        return "{\"state\":" + ret + "}";
    }

    private String check(JSONObject jo) {
        AppInvServer appInvServer = PlatformInvServer.getAppInvServer(appConfig.getAppName());
        boolean ret = false;
        if (appInvServer != null) {
            ret = appInvServer.check(jo);
        }
        return "{\"state\":" + ret + "}";
    }
}
