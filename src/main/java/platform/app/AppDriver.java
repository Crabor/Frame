package platform.app;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import platform.comm.pubsub.AbstractSubscriber;
import platform.comm.socket.TCP;
import platform.comm.socket.UDP;
import platform.config.AppConfig;
import platform.config.Configuration;
import platform.config.SensorConfig;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
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
                    JSONObject retJson = new JSONObject();
                    retJson.put("state", true);
                    retJson.put("udp_port", clientUDPPort);
                    //TODO: 还有一些初始化工作
                    Configuration.getAppsConfig().put(appName, new AppConfig(appName));
                    tcp.send(retJson.toJSONString());
                } else if (api.equalsIgnoreCase("cancel_app")) {
                    //TODO: 一些析构工作

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
                    for (Map.Entry<String, SensorConfig> entry : Configuration.getResourceConfig().getSensorsConfig().entrySet()) {
                        if (!entry.getValue().getApps().contains(appName)) {
                            continue;
                        }
                        String sensorName = entry.getKey();
                        SensorConfig config = entry.getValue();
                        JSONObject joo = new JSONObject(3);
                        joo.put("sensor_name", sensorName);
                        joo.put("state", config.isAlive() ? "on" : "off");
                        joo.put("value_type", config.getSensorType());
                        retJsonArray.add(joo);
                    }
                    tcp.send(retJsonArray.toJSONString());
                } else if (api.equalsIgnoreCase("register_sensor")) {
                    JSONObject retJson = new JSONObject();
                    String sensorName = jo.getString("sensor_name");
                    //TODO:检查sensorName是否合法
                    Configuration.getAppsConfig().get(appName).registerSensor(sensorName);
                    retJson.put("state", true);
                    tcp.send(retJson.toJSONString());
                } else if (api.equalsIgnoreCase("cancel_sensor")) {
                    JSONObject retJson = new JSONObject();
                    String sensorName = jo.getString("sensor_name");
                    Configuration.getAppsConfig().get(appName).cancelSensor(sensorName);
                    retJson.put("state", true);
                    tcp.send(retJson.toJSONString());
                } else if (api.equalsIgnoreCase("get_supported_actuators")) {

                } else if (api.equalsIgnoreCase("get_registered_actuators")) {

                } else if (api.equalsIgnoreCase("register_actuator")) {

                } else if (api.equalsIgnoreCase("cancel_actuator")) {

                } else if (api.equalsIgnoreCase("set_actuator")) {

                } else if (api.equalsIgnoreCase("is_server_on")) {

                } else if (api.equalsIgnoreCase("call")) {

                } else if (api.equalsIgnoreCase("set_rule_file")) {

                } else if (api.equalsIgnoreCase("set_pattern_file")) {

                } else if (api.equalsIgnoreCase("set_bfunc_file")) {

                } else if (api.equalsIgnoreCase("set_mfunc_file")) {

                } else if (api.equalsIgnoreCase("set_rfunc_file")) {

                } else if (api.equalsIgnoreCase("set_ctx_validator")) {

                }
            }
        } finally {
            tcp.close();
        }
    }
}
