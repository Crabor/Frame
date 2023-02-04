package platform.app;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import platform.Platform;
import platform.communication.pubsub.AbstractSubscriber;
import platform.communication.socket.Cmd;
import platform.communication.socket.PlatformUDP;
import common.socket.TCP;
import common.socket.UDP;
import platform.config.AppConfig;
import platform.config.Configuration;
import common.struct.CmdType;
import common.struct.ServiceType;
import common.util.Util;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class AppDriver extends AbstractSubscriber implements Runnable {
    private TCP tcp;
    private String clientIP;
    private int clientUDPPort;
    private int grpId;

    public AppDriver(Socket socket) {
        this.tcp = new TCP(socket);
    }

    @Override
    public void onMessage(String channel, String msg) {
        JSONObject jo = new JSONObject(2);
        jo.put("channel", channel);
        jo.put("msg", msg);
        //TODO:
        UDP.send(clientIP, clientUDPPort, jo.toJSONString());
    }

    private static final String CTX_FILE_PATH = "Resources/configFile/ctxFile";

    @Override
    public void run() {
        String msgFromClient;
        while ((msgFromClient = tcp.recv()) != null) {
            // TODO:
            JSONObject jo = JSON.parseObject(msgFromClient);
            logger.info(jo.toJSONString());
            String api = jo.getString("api");
            String appName = jo.getString("app_name");

            if (api.equalsIgnoreCase("register_app")) {
                clientIP = tcp.getSocket().getInetAddress().getHostAddress();
                clientUDPPort = AppMgrThread.getNewPort(tcp.getSocket());
                grpId = AppMgrThread.getNewGrpId();
                JSONObject retJson = new JSONObject(2);
                retJson.put("state", true);
                retJson.put("udp_port", clientUDPPort);
                //TODO: 还有一些初始化工作
                Configuration.getAppsConfig().put(appName, new AppConfig(appName));
                tcp.send(retJson.toJSONString());
            } else if (api.equalsIgnoreCase("cancel_app")) {
                //TODO: 一些析构工作
                AppConfig appConfig = Configuration.getAppsConfig().get(appName);
                appConfig.getSensors().forEach(sensorConfig -> {
                    sensorConfig.removeApp(appConfig);
                    appConfig.removeSensor(sensorConfig);
                    unsubscribe(sensorConfig.getSensorName());
                });
                appConfig.getActuators().forEach(actuatorConfig -> {
                    actuatorConfig.removeApp(appConfig);
                    appConfig.removeActuator(actuatorConfig);
                });
                Configuration.getAppsConfig().remove(appName);
                AppMgrThread.removePort(tcp.getSocket(), clientUDPPort);
                AppMgrThread.removeGrpId(grpId);
                //TODO:server相关资源取消
                JSONObject retJson = new JSONObject(1);
                retJson.put("state", true);
                tcp.send(retJson.toJSONString());
                tcp.close();
//
//                logger.info("\n" + appName + " exit!");
//                logger.info("sensors:");
//                Configuration.getResourceConfig().getSensorsConfig().forEach((s, config) -> {
//                    logger.info(s + " -> " + config.getAppsName());
//                });
//                logger.info("actuators:");
//                Configuration.getResourceConfig().getActuatorsConfig().forEach((s, config) -> {
//                    logger.info(s + " -> " + config.getAppsName());
//                });
//                logger.info("apps:");
//                Configuration.getAppsConfig().forEach((s, config) -> {
//                    logger.info(s + " -> sensors:" + config.getSensorsName() + ", actuators:" + config.getActuatorsName());
//                });
            } else if (api.equalsIgnoreCase("get_supported_sensors")) {
                JSONArray retJsonArray = new JSONArray();
                Configuration.getResourceConfig().getSensorsConfig().forEach((sensorName, config) -> {
                    JSONObject joo = new JSONObject(3);
                    joo.put("sensor_name", sensorName);
                    joo.put("state", config.isAlive() ? "on" : "off");
                    joo.put("value_type", config.getSensorType());
                    retJsonArray.add(joo);
                });
                tcp.send(retJsonArray.toJSONString());
            } else if (api.equalsIgnoreCase("get_registered_sensors")) {
                JSONArray retJsonArray = new JSONArray();
                AppConfig appConfig = Configuration.getAppsConfig().get(appName);
                appConfig.getSensors().forEach(config -> {
                    JSONObject joo = new JSONObject(3);
                    joo.put("sensor_name", config.getSensorName());
                    joo.put("state", config.isAlive() ? "on" : "off");
                    joo.put("value_type", config.getSensorType());
                    retJsonArray.add(joo);
                });
                tcp.send(retJsonArray.toJSONString());
            } else if (api.equalsIgnoreCase("register_sensor")) {
                JSONObject retJson = new JSONObject(1);
                String sensorName = jo.getString("sensor_name");
                //TODO:检查sensorName是否合法
                Configuration.getAppsConfig().get(appName).registerSensor(sensorName);
//                    logger.info("sensors:\n" + Configuration.getAppsConfig().get(appName).getSensors());
                subscribe(sensorName, grpId);
                retJson.put("state", true);
                tcp.send(retJson.toJSONString());
            } else if (api.equalsIgnoreCase("cancel_sensor")) {
                JSONObject retJson = new JSONObject(1);
                String sensorName = jo.getString("sensor_name");
                Configuration.getAppsConfig().get(appName).cancelSensor(sensorName);
                unsubscribe(sensorName);
                retJson.put("state", true);
                tcp.send(retJson.toJSONString());
            } else if (api.equalsIgnoreCase("get_supported_actuators")) {
                JSONArray retJsonArray = new JSONArray();
                Configuration.getResourceConfig().getActuatorsConfig().forEach((actuatorName, config) -> {
                    JSONObject joo = new JSONObject(3);
                    joo.put("actuator_name", actuatorName);
                    joo.put("state", config.isAlive() ? "on" : "off");
                    joo.put("value_type", config.getActuatorType());
                    retJsonArray.add(joo);
                });
                tcp.send(retJsonArray.toJSONString());
            } else if (api.equalsIgnoreCase("get_registered_actuators")) {
                JSONArray retJsonArray = new JSONArray();
                AppConfig appConfig = Configuration.getAppsConfig().get(appName);
                appConfig.getActuators().forEach(config -> {
                    JSONObject joo = new JSONObject(3);
                    joo.put("actuator_name", config.getActuatorName());
                    joo.put("state", config.isAlive() ? "on" : "off");
                    joo.put("value_type", config.getActuatorType());
                    retJsonArray.add(joo);
                });
                tcp.send(retJsonArray.toJSONString());
            } else if (api.equalsIgnoreCase("register_actuator")) {
                JSONObject retJson = new JSONObject(1);
                String actuatorName = jo.getString("actuator_name");
                //TODO:检查sensorName是否合法
                Configuration.getAppsConfig().get(appName).registerSensor(actuatorName);
                retJson.put("state", true);
                tcp.send(retJson.toJSONString());
            } else if (api.equalsIgnoreCase("cancel_actuator")) {
                JSONObject retJson = new JSONObject(1);
                String actuatorName = jo.getString("actuator_name");
                //TODO:检查sensorName是否合法
                Configuration.getAppsConfig().get(appName).cancelActuator(actuatorName);
                retJson.put("state", true);
                tcp.send(retJson.toJSONString());
            } else if (api.equalsIgnoreCase("set_actuator")) {
                JSONObject retJson = new JSONObject(1);
                String actuatorName = jo.getString("actuator_name");
                String action = jo.getString("action");
                PlatformUDP.send(new Cmd("actuator_set", actuatorName, action));
                retJson.put("state", true);
                tcp.send(retJson.toJSONString());
            } else if (api.equalsIgnoreCase("is_server_on")) {
                //TODO:是appCtxServer还是PlatformCtxServer
            } else if (api.equalsIgnoreCase("call")) {
                JSONObject retJson = new JSONObject(1);
                ServiceType serviceType = Util.parseServiceType(jo.getString("service_type"));
                CmdType cmdType = Util.parseCmdType(jo.getString("cmd_type"));
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
                String path = CTX_FILE_PATH + "/" + appName + "/" + jo.getString("file_name");
                String content = jo.getString("content");
                Util.writeFileContent(path, content);
                Configuration.getAppsConfig().get(appName).setRuleFile(path);
                JSONObject retJson = new JSONObject(1);
                retJson.put("state", true);
                tcp.send(retJson.toJSONString());
            } else if (api.equalsIgnoreCase("set_pattern_file")) {
                String path = CTX_FILE_PATH + "/" + appName + "/" + jo.getString("file_name");
                String content = jo.getString("content");
                Util.writeFileContent(path, content);
                Configuration.getAppsConfig().get(appName).setPatternFile(path);
                JSONObject retJson = new JSONObject(1);
                retJson.put("state", true);
                tcp.send(retJson.toJSONString());
            } else if (api.equalsIgnoreCase("set_bfunc_file")) {
                String path = CTX_FILE_PATH + "/" + appName + "/" + jo.getString("file_name");
                String content = jo.getString("content");
                Util.writeFileContent(path, content);
                Configuration.getAppsConfig().get(appName).setBfuncFile(path);
                JSONObject retJson = new JSONObject(1);
                retJson.put("state", true);
                tcp.send(retJson.toJSONString());
            } else if (api.equalsIgnoreCase("set_mfunc_file")) {
                String path = CTX_FILE_PATH + "/" + appName + "/" + jo.getString("file_name");
                String content = jo.getString("content");
                Util.writeFileContent(path, content);
                Configuration.getAppsConfig().get(appName).setMfuncFile(path);
                JSONObject retJson = new JSONObject(1);
                retJson.put("state", true);
                tcp.send(retJson.toJSONString());
            } else if (api.equalsIgnoreCase("set_rfunc_file")) {
                //TODO:ctx还未实现该功能
            } else if (api.equalsIgnoreCase("set_ctx_validator")) {
                String ctxValidator = jo.getString("ctx_validator");
                Configuration.getAppsConfig().get(appName).setCtxValidator(ctxValidator);
                JSONObject retJson = new JSONObject(1);
                retJson.put("state", true);
                tcp.send(retJson.toJSONString());
            }
        }
        tcp.close();
    }
}
