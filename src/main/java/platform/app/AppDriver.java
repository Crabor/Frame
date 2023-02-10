package platform.app;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import platform.Platform;
import platform.communication.pubsub.AbstractSubscriber;
import platform.communication.socket.Cmd;
import platform.communication.socket.PlatformUDP;
import common.socket.TCP;
import platform.config.ActuatorConfig;
import platform.config.AppConfig;
import platform.config.Configuration;
import common.struct.CmdType;
import common.struct.ServiceType;
import common.util.Util;
import platform.config.SensorConfig;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sun.tools.javac.Main;

public class AppDriver extends AbstractSubscriber implements Runnable {
    private TCP tcp;
//    private String clientIP;
//    private int clientUDPPort;
    private int grpId;
    private ConcurrentHashMap<String, String> values = new ConcurrentHashMap<>();

    public AppDriver(Socket socket) {
        this.tcp = new TCP(socket);
    }

    @Override
    public void onMessage(String channel, String msg) {
//        JSONObject jo = new JSONObject(2);
//        jo.put("channel", channel);
//        jo.put("msg", msg);
        //TODO:
//        UDP.send(clientIP, clientUDPPort, jo.toJSONString());
//        logger.info(String.format("appDriver onmessage: %s, %s", channel, msg));
//        JSONObject jo = JSON.parseObject(msg);
        values.put(channel, msg);
//        logger.info("onMessage: " + values);
    }

    private static final String CTX_FILE_PATH = "Resources/configFile/ctxFile";

    @Override
    public void run() {
        String msgFromClient;
        AppConfig appConfig = null;
        Map<String, SensorConfig> sensorConfigMap = Configuration.getResourceConfig().getSensorsConfig();
        Map<String, ActuatorConfig> actuatorConfigMap = Configuration.getResourceConfig().getActuatorsConfig();
        while ((msgFromClient = tcp.recv()) != null) {
            // TODO:
            JSONObject jo = JSON.parseObject(msgFromClient);
            logger.info(jo.toJSONString());
            String api = jo.getString("api");
            String appName = jo.getString("app_name");

            if (api.equalsIgnoreCase("connect")) {
//                clientIP = tcp.getSocket().getInetAddress().getHostAddress();
//                clientUDPPort = AppMgrThread.getNewPort(tcp.getSocket());
                grpId = AppMgrThread.getNewGrpId();
                JSONObject retJson = new JSONObject(1);
                retJson.put("state", true);
//                retJson.put("udp_port", clientUDPPort);
                //TODO: 还有一些初始化工作
                appConfig = new AppConfig(appName);
                appConfig.setGrpId(grpId);
                Configuration.getAppsConfig().put(appName, appConfig);
                tcp.send(retJson.toJSONString());
            } else if (api.equalsIgnoreCase("disconnect")) {
                //TODO: 一些析构工作
                JSONObject retJson = new JSONObject(1);
                AppConfig finalAppConfig = appConfig;
                if (appConfig != null) {
                    appConfig.getSensorsName().forEach(sensorName -> {
                        finalAppConfig.cancelSensor(sensorName);
                        unsubscribe(sensorName);
                    });
                    appConfig.getActuatorsName().forEach(appConfig::cancelActuator);
                    Configuration.getAppsConfig().remove(appName);
                    appConfig = null;
                    AppMgrThread.removeGrpId(grpId);
                    retJson.put("state", true);
                } else {
                    retJson.put("state", false);
                }
                //TODO:server相关资源取消
                tcp.send(retJson.toJSONString());
                tcp.close();
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
//                logger.info("apps:");
//                appConfigMap.forEach((s, config) -> {
//                    logger.info(s + " -> sensors:" + config.getSensorsName() + ", actuators:" + config.getActuatorsName());
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
            } else if (api.equalsIgnoreCase("get_sensor_freq")) {
                JSONObject retJson = new JSONObject(1);
                String sensorName = jo.getString("sensor_name");
                if (sensorConfigMap.containsKey(sensorName)) {
                    retJson.put("freq", sensorConfigMap.get(sensorName).getValueFreq());
                } else {
                    retJson.put("freq", -1);
                }
                tcp.send(retJson.toJSONString());
            } else if (api.equalsIgnoreCase("is_sensor_registered")) {
                JSONObject retJson = new JSONObject(1);
                String sensorName = jo.getString("sensor_name");
                if (appConfig != null && sensorConfigMap.containsKey(sensorName)) {
                    retJson.put("state", appConfig.getSensorsName().contains(sensorName));
                } else {
                    retJson.put("state", false);
                }
                tcp.send(retJson.toJSONString());
            } else if (api.equalsIgnoreCase("register_sensor")) {
                JSONObject retJson = new JSONObject(1);
                String sensorName = jo.getString("sensor_name");
                if (appConfig != null && sensorConfigMap.containsKey(sensorName)) {
                    appConfig.registerSensor(sensorName);
//                    logger.info("sensors:\n" + appConfig.getSensors());
                    subscribe(sensorName, grpId);
                    retJson.put("state", true);
                } else {
                    retJson.put("state", false);
                }
                tcp.send(retJson.toJSONString());
            } else if (api.equalsIgnoreCase("cancel_sensor")) {
                JSONObject retJson = new JSONObject(1);
                String sensorName = jo.getString("sensor_name");
                if (appConfig != null && appConfig.getSensorsName().contains(sensorName)) {
                    appConfig.cancelSensor(sensorName);
                    unsubscribe(sensorName);
                    SensorConfig sensorConfig = sensorConfigMap.get(sensorName);
                    if (sensorConfig.getApps().isEmpty()) {
                        sensorConfig.stopGetValue();
                    }
                    retJson.put("state", true);
                } else {
                    retJson.put("state", false);
                }
                tcp.send(retJson.toJSONString());
            } else if (api.equalsIgnoreCase("cancel_all_sensors")) {
                JSONObject retJson = new JSONObject(1);
                if (appConfig != null) {
                    AppConfig finalAppConfig1 = appConfig;
                    appConfig.getSensorsName().forEach(sensorName -> {
                        finalAppConfig1.cancelSensor(sensorName);
                        unsubscribe(sensorName);
                        SensorConfig sensorConfig = sensorConfigMap.get(sensorName);
                        if (sensorConfig.getApps().isEmpty()) {
                            sensorConfig.stopGetValue();
                        }
                    });
                    retJson.put("state", true);
                } else {
                    retJson.put("state", false);
                };
                tcp.send(retJson.toJSONString());
            } else if (api.equalsIgnoreCase("get_sensor_data")) {
                JSONObject retJson = new JSONObject(1);
                String sensorName = jo.getString("sensor_name");
                if (appConfig != null && appConfig.getSensorsName().contains(sensorName)) {
                    while (values.get(sensorName) == null) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
//                    logger.info("get_sensor_data: " + values);
                    }

                    retJson.put("value", values.get(sensorName));
                } else {
                    retJson.put("value", "@#$%");
                }

//                logger.info("platform send: " + retJson);
                tcp.send(retJson.toJSONString());
            } else if (api.equalsIgnoreCase("get_all_sensor_data")) {
                JSONArray ja = new JSONArray();
                values.forEach((sensorName, value) -> {
                    JSONObject joo = new JSONObject(2);
                    joo.put("sensor_name", sensorName);
                    joo.put("value", value);
                    ja.add(joo);
                });
                tcp.send(ja.toJSONString());
            } else if (api.equalsIgnoreCase("get_supported_actuators")) {
                JSONArray retJsonArray = new JSONArray();
                actuatorConfigMap.forEach((actuatorName, config) -> {
                    JSONObject joo = new JSONObject(3);
                    joo.put("actuator_name", actuatorName);
                    joo.put("state", config.isAlive() ? "on" : "off");
                    joo.put("value_type", config.getActuatorType());
                    retJsonArray.add(joo);
                });
                tcp.send(retJsonArray.toJSONString());
            } else if (api.equalsIgnoreCase("get_registered_actuators")) {
                JSONArray retJsonArray = new JSONArray();
                if (appConfig != null) {
                    appConfig.getActuators().forEach(config -> {
                        JSONObject joo = new JSONObject(3);
                        joo.put("actuator_name", config.getActuatorName());
                        joo.put("state", config.isAlive() ? "on" : "off");
                        joo.put("value_type", config.getActuatorType());
                        retJsonArray.add(joo);
                    });
                }

                tcp.send(retJsonArray.toJSONString());
            } else if (api.equalsIgnoreCase("get_registered_actuators_status")) {
                JSONObject retJson = new JSONObject(1);
                //TODO:检查sensorName是否合法
                boolean state = true;
                if (appConfig != null) {
                    for (ActuatorConfig config : appConfig.getActuators()) {
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
                    appConfig.registerActuator(actuatorName);
                    retJson.put("state", true);
                } else {
                    retJson.put("state", false);
                }
                tcp.send(retJson.toJSONString());
            } else if (api.equalsIgnoreCase("cancel_actuator")) {
                JSONObject retJson = new JSONObject(1);
                String actuatorName = jo.getString("actuator_name");
                if (appConfig != null && appConfig.getActuatorsName().contains(actuatorName)) {
                    appConfig.cancelActuator(actuatorName);
                    retJson.put("state", true);
                } else {
                    retJson.put("state", false);
                }
                tcp.send(retJson.toJSONString());
            } else if (api.equalsIgnoreCase("cancel_all_actuators")) {
                JSONObject retJson = new JSONObject(1);
                if (appConfig != null) {
                    appConfig.getActuatorsName().forEach(appConfig::cancelActuator);
                    retJson.put("state", true);
                } else {
                    retJson.put("state", false);
                }
                tcp.send(retJson.toJSONString());
            } else if (api.equalsIgnoreCase("set_actuator")) {
                JSONObject retJson = new JSONObject(1);
                String actuatorName = jo.getString("actuator_name");
                if (appConfig != null
                        && appConfig.getActuatorsName().contains(actuatorName)
                        && actuatorConfigMap.get(actuatorName).isAlive()) {
                    String action = jo.getString("action");
                    PlatformUDP.send(new Cmd("actuator_set", actuatorName, action));
                    retJson.put("state", true);
                } else {
                    retJson.put("state", false);
                }
                tcp.send(retJson.toJSONString());
            } else if (api.equalsIgnoreCase("is_server_on")) {
                //TODO:是appCtxServer还是PlatformCtxServer
            } else if (api.equalsIgnoreCase("call")) {
                JSONObject retJson = new JSONObject(1);
                ServiceType serviceType = ServiceType.fromString(jo.getString("service_type"));
                CmdType cmdType = CmdType.fromString(jo.getString("cmd_type"));
                JSONArray argsJsonArray = jo.getJSONArray("args");
                List<String> args = new ArrayList<>();
                for (Object joo : argsJsonArray) {
                    JSONObject arg = (JSONObject) joo;
                    args.add(arg.getString("arg"));
                }
                String ret = Platform.call(appName, serviceType, cmdType, args.toArray(new String[0]));
                retJson.put("ret", ret);
                tcp.send(retJson.toJSONString());
            } else if (api.equalsIgnoreCase("set_rule_file")) {
                JSONObject retJson = new JSONObject(1);
                String content = jo.getString("content");
                String dir = CTX_FILE_PATH + "/" + appName;
                Util.writeFileContent(dir, jo.getString("file_name"), content);
                if (appConfig != null) {
                    appConfig.setRuleFile(dir + "/" + jo.getString("file_name"));
                    retJson.put("state", true);
                } else {
                    retJson.put("state", false);
                }
                tcp.send(retJson.toJSONString());
            } else if (api.equalsIgnoreCase("set_pattern_file")) {
                JSONObject retJson = new JSONObject(1);
                String content = jo.getString("content");
                String dir = CTX_FILE_PATH + "/" + appName;
                Util.writeFileContent(dir, jo.getString("file_name"), content);
                if (appConfig != null) {
                    appConfig.setPatternFile(dir + "/" + jo.getString("file_name"));
                    retJson.put("state", true);
                } else {
                    retJson.put("state", false);
                }
                tcp.send(retJson.toJSONString());
            } else if (api.equalsIgnoreCase("set_bfunc_file")) {
                JSONObject retJson = new JSONObject(1);
                String content = jo.getString("content");
                String dir = CTX_FILE_PATH + "/" + appName;
                String fileName = jo.getString("file_name");
                String javaFile = fileName.replace(".class", ".java");
                Util.writeFileContent(dir, javaFile, content);
                String[] args = new String[] {dir + "/" + javaFile};
                if(Main.compile(args) == 0 && appConfig != null) {
                    appConfig.setBfuncFile(dir + "/" + fileName);
                    retJson.put("state", true);
                } else {
                    retJson.put("state", false);
                }
                tcp.send(retJson.toJSONString());
            } else if (api.equalsIgnoreCase("set_mfunc_file")) {
                JSONObject retJson = new JSONObject(1);
                String content = jo.getString("content");
                String dir = CTX_FILE_PATH + "/" + appName;
                String fileName = jo.getString("file_name");
                String javaFile = fileName.replace(".class", ".java");
                Util.writeFileContent(dir, javaFile, content);
                String[] args = new String[] {dir + "/" + javaFile};
                if(Main.compile(args) == 0 && appConfig != null) {
                    appConfig.setMfuncFile(dir + "/" + fileName);
                    retJson.put("state", true);
                } else {
                    retJson.put("state", false);
                }
                tcp.send(retJson.toJSONString());
            } else if (api.equalsIgnoreCase("set_rfunc_file")) {
                //TODO:ctx还未实现该功能
            } else if (api.equalsIgnoreCase("set_ctx_validator")) {
                JSONObject retJson = new JSONObject(1);
                String ctxValidator = jo.getString("ctx_validator");
                if (appConfig != null) {
                    appConfig.setCtxValidator(ctxValidator);
                    retJson.put("state", true);
                } else {
                    retJson.put("state", false);
                }
                tcp.send(retJson.toJSONString());
            }
        }
        tcp.close();
    }
}
