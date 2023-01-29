package platform.app;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import platform.Platform;
import platform.comm.pubsub.AbstractSubscriber;
import platform.comm.socket.Cmd;
import platform.comm.socket.PlatformUDP;
import platform.comm.socket.TCP;
import platform.comm.socket.UDP;
import platform.config.ActuatorConfig;
import platform.config.AppConfig;
import platform.config.Configuration;
import platform.config.SensorConfig;
import platform.struct.CmdType;
import platform.struct.ServiceType;
import platform.util.Util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AppDriver extends AbstractSubscriber implements Runnable {
    private TCP tcp;
    private String clientIP;
    private int clientUDPPort;

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
        try {
            String msgFromClient;
            while ((msgFromClient = tcp.recv()) != null) {
                // TODO:
                JSONObject jo = JSON.parseObject(msgFromClient);
                String api = jo.getString("api");
                String appName = jo.getString("app_name");

                if (api.equalsIgnoreCase("register_app")) {
                    clientIP = tcp.getSocket().getInetAddress().getHostAddress();
                    clientUDPPort = AppMgrThread.getNewSensorDataChannelUDPPort(tcp.getSocket());
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
                    });
                    appConfig.getActuators().forEach(actuatorConfig -> {
                        actuatorConfig.removeApp(appConfig);
                        appConfig.removeActuator(actuatorConfig);
                    });
                    Configuration.getAppsConfig().remove(appName);
                    //TODO:server相关资源取消
                    JSONObject retJson = new JSONObject(1);
                    retJson.put("state", true);
                    tcp.send(retJson.toJSONString());
                    tcp.close();
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
                    Map<String, SensorConfig> sensorConfigMap = Configuration.getResourceConfig().getSensorsConfig();
                    AppConfig appConfig = Configuration.getAppsConfig().get(appName);
                    appConfig.getSensors().forEach(sensorName -> {
                        SensorConfig config = sensorConfigMap.get(sensorName);
                        JSONObject joo = new JSONObject(3);
                        joo.put("sensor_name", sensorName);
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
                    retJson.put("state", true);
                    tcp.send(retJson.toJSONString());
                } else if (api.equalsIgnoreCase("cancel_sensor")) {
                    JSONObject retJson = new JSONObject(1);
                    String sensorName = jo.getString("sensor_name");
                    Configuration.getAppsConfig().get(appName).cancelSensor(sensorName);
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
                    Map<String, ActuatorConfig> actuatorConfigMap = Configuration.getResourceConfig().getActuatorsConfig();
                    AppConfig appConfig = Configuration.getAppsConfig().get(appName);
                    appConfig.getActuators().forEach(actuatorName -> {
                        ActuatorConfig config = actuatorConfigMap.get(actuatorName);
                        JSONObject joo = new JSONObject(3);
                        joo.put("actuator_name", actuatorName);
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
        } finally {
            tcp.close();
        }
    }
}
