package app;

import app.struct.ActuatorInfo;
import app.struct.SensorInfo;
import app.struct.State;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import common.socket.TCP;
import common.socket.UDP;
import common.struct.CmdType;
import common.struct.ServiceType;
import common.util.Util;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractApp implements App {
    private TCP tcp;
    protected Log logger = LogFactory.getLog(getClass());
    protected String appName;

    public AbstractApp() {
        appName = getClass().getName();
    }

    //API:

    //app:
    UDPThread udpThread = null;
    public class UDPThread extends Thread {
        private volatile boolean shouldStop = false;
        private final int udpPort;

        public UDPThread(int port) {
            this.udpPort = port;
        }

        public void run() {
            while (!shouldStop) {
                JSONObject sensorJson = JSON.parseObject(UDP.recv(udpPort));
                if (sensorJson != null) {
                    String channel = sensorJson.getString("channel");
                    String msg = sensorJson.getString("msg");
                    getMsg(channel, msg);
                }
            }
        }

        public void stopThread() {
            shouldStop = true;
            UDP.close(udpPort);
        }
    }

    public boolean registerApp(String ip, int port) {
        JSONObject jo = new JSONObject(4);
        jo.put("api", "register_app");
        jo.put("app_name", appName);
        boolean state = false;
        try {
            tcp = new TCP(new Socket(ip, port));
            tcp.send(jo.toJSONString());

            JSONObject retJson = JSON.parseObject(tcp.recv());
            state = retJson.getBooleanValue("state");
            int udpPort = retJson.getIntValue("udp_port");
            udpThread = new UDPThread(udpPort);
            udpThread.start();
        } catch (IOException e) {
            e.printStackTrace();
            tcp.close();
        }
        logger.info(String.format("[%s]: registerApp(%s, %d) -> %s", appName, ip, port, state));
        return state;
    }

    public boolean cancelApp() {
        JSONObject jo = new JSONObject(2);
        jo.put("api", "cancel_app");
        jo.put("app_name", appName);
        tcp.send(jo.toJSONString());
        JSONObject retJson = JSON.parseObject(tcp.recv());
        if (udpThread != null) {
            udpThread.stopThread();
        }
        tcp.close();
        Boolean state = retJson.getBoolean("state");
        logger.info(String.format("[%s]: cancelApp() -> %s", appName, state));
        return state;
    }

    //sensor:
    public boolean registerSensor(String sensorName) {
        JSONObject jo = new JSONObject(3);
        jo.put("api", "register_sensor");
        jo.put("app_name", appName);
        jo.put("sensor_name", sensorName);
        tcp.send(jo.toJSONString());
        JSONObject retJson = JSON.parseObject(tcp.recv());
        Boolean state = retJson.getBoolean("state");
        logger.info(String.format("[%s]: registerSensor(%s) -> %s", appName, sensorName, state));
        return state;
    }

    public boolean cancelSensor(String sensorName) {
        JSONObject jo = new JSONObject(3);
        jo.put("api", "cancel_sensor");
        jo.put("app_name", appName);
        jo.put("sensor_name", sensorName);
        tcp.send(jo.toJSONString());
        JSONObject retJson = JSON.parseObject(tcp.recv());
        Boolean state = retJson.getBoolean("state");
        logger.info(String.format("[%s]: cancelSensor(%s) -> %s", appName, sensorName, state));
        return state;
    }

    public Map<String, SensorInfo> getRegisteredSensors() {
        JSONObject jo = new JSONObject(2);
        jo.put("api", "get_registered_sensors");
        jo.put("app_name", appName);
        tcp.send(jo.toJSONString());
        JSONArray retJson = JSON.parseArray(tcp.recv());

        Map<String, SensorInfo> ret = new HashMap<>();
        retJson.forEach(obj -> {
            JSONObject joo = (JSONObject) obj;
            ret.put(joo.getString("sensor_name"), new SensorInfo(joo));
        });
        logger.info(String.format("[%s]: getRegisteredSensors() -> %s", appName, ret));
        return ret;
    }

    public Map<String, SensorInfo> getSupportedSensors() {
        JSONObject jo = new JSONObject(2);
        jo.put("api", "get_supported_sensors");
        jo.put("app_name", appName);
        tcp.send(jo.toJSONString());
        JSONArray retJson = JSON.parseArray(tcp.recv());

        Map<String, SensorInfo> ret = new HashMap<>();
        retJson.forEach(obj -> {
            JSONObject joo = (JSONObject) obj;
            ret.put(joo.getString("sensor_name"), new SensorInfo(joo));
        });
        logger.info(String.format("[%s]: getSupportedSensors() -> %s", appName, ret));
        return ret;
    }

    //actuator:
    public boolean registerActuator(String actuatorName) {
        JSONObject jo = new JSONObject(3);
        jo.put("api", "register_actuator");
        jo.put("app_name", appName);
        jo.put("sensor_name", actuatorName);
        tcp.send(jo.toJSONString());
        JSONObject retJson = JSON.parseObject(tcp.recv());
        Boolean state = retJson.getBoolean("state");
        logger.info(String.format("[%s]: registerActuator(%s) -> %s", appName, actuatorName, state));
        return state;
    }

    public boolean cancelActuator(String actuatorName) {
        JSONObject jo = new JSONObject(3);
        jo.put("api", "cancel_actuator");
        jo.put("app_name", appName);
        jo.put("sensor_name", actuatorName);
        tcp.send(jo.toJSONString());
        JSONObject retJson = JSON.parseObject(tcp.recv());
        Boolean state = retJson.getBoolean("state");
        logger.info(String.format("[%s]: cancelActuator(%s) -> %s", appName, actuatorName, state));
        return state;
    }

    public Map<String, ActuatorInfo> getSupportedActuators() {
        JSONObject jo = new JSONObject(2);
        jo.put("api", "get_supported_actuators");
        jo.put("app_name", appName);
        tcp.send(jo.toJSONString());
        JSONArray retJson = JSON.parseArray(tcp.recv());

        Map<String, ActuatorInfo> ret = new HashMap<>();
        retJson.forEach(obj -> {
            JSONObject joo = (JSONObject) obj;
            ret.put(joo.getString("actuator_name"), new ActuatorInfo(joo));
        });
        logger.info(String.format("[%s]: getSupportedActuators() -> %s", appName, ret));
        return ret;
    }

    public Map<String, ActuatorInfo> getRegisteredActuators() {
        JSONObject jo = new JSONObject(2);
        jo.put("api", "get_supported_actuators");
        jo.put("app_name", appName);
        tcp.send(jo.toJSONString());
        JSONArray retJson = JSON.parseArray(tcp.recv());

        Map<String, ActuatorInfo> ret = new HashMap<>();
        retJson.forEach(obj -> {
            JSONObject joo = (JSONObject) obj;
            ret.put(joo.getString("actuator_name"), new ActuatorInfo(joo));
        });
        logger.info(String.format("[%s]: getRegisteredActuators() -> %s", appName, ret));
        return ret;
    }

    public boolean setActuator(String actuatorName, String action) {
        JSONObject jo = new JSONObject(4);
        jo.put("api", "set_actuator");
        jo.put("app_name", appName);
        jo.put("actuator_name", actuatorName);
        jo.put("action", action);
        tcp.send(jo.toJSONString());
        JSONObject retJson = JSON.parseObject(tcp.recv());
        Boolean state = retJson.getBoolean("state");
        logger.info(String.format("[%s]: setActuator(%s, %s) -> %s", appName, actuatorName, action, state));
        return state;
    }

    //service:
    public boolean isServerOn(ServiceType type) {
        JSONObject jo = new JSONObject(3);
        jo.put("api", "is_server_on");
        jo.put("app_name", appName);
        jo.put("service_type", type.toString());
        tcp.send(jo.toJSONString());
        JSONObject retJson = JSON.parseObject(tcp.recv());
        Boolean state = retJson.getBoolean("state");
        logger.info(String.format("[%s]: isServerOn(%s) -> %s", appName, type, state));
        return state;
    }

    //TODO: 由于改成进程间通信（网络套接字）而不是进程内部函数调用，所以参数返回值不能为Object
    public String call(ServiceType serviceType, CmdType cmdType, String... args) {
        JSONObject jo = new JSONObject(5);
        jo.put("api", "call");
        jo.put("app_name", appName);
        jo.put("service_type", serviceType.toString());
        jo.put("cmd_type", cmdType.toString());
        JSONArray ja = new JSONArray();
        for (String arg : args) {
            JSONObject joo = new JSONObject(1);
            joo.put("arg", arg);
            ja.add(joo);
        }
        jo.put("args", ja);
        tcp.send(jo.toJSONString());
        JSONObject retJson = JSON.parseObject(tcp.recv());

        String ret = retJson.getString("state");
        logger.info(String.format("[%s]: call(%s, %s, %s) -> %s", appName, serviceType, cmdType, Arrays.toString(args), ret));
        return ret;
    }

    //ctx:
    //TODO:假如应用和平台是处于不同的主机，那么ruleFile是存在平台所在主机还是存在应用所在主机？
    // 如果存在应用所在主机的话，那么以下命令将会将对应文件内容全部加载到字符串然后通过网络发送到平台，网络通信压力？
    public boolean setRuleFile(String ruleFile) {
        String content = Util.readFileContent(ruleFile);

        JSONObject jo = new JSONObject(3);
        jo.put("api", "set_rule_file");
        jo.put("app_name", appName);
        jo.put("file_name", Util.getSimpleFileName(ruleFile));
        jo.put("content", content);
        tcp.send(jo.toJSONString());
        JSONObject retJson = JSON.parseObject(tcp.recv());
        Boolean state = retJson.getBoolean("state");
        logger.info(String.format("[%s]: setRuleFile(%s) -> %s", appName, ruleFile, state));
        return state;
    }

    public boolean setPatternFile(String patternFile) {
        String content = Util.readFileContent(patternFile);

        JSONObject jo = new JSONObject(3);
        jo.put("api", "set_pattern_file");
        jo.put("app_name", appName);
        jo.put("file_name", Util.getSimpleFileName(patternFile));
        jo.put("content", content);
        tcp.send(jo.toJSONString());
        JSONObject retJson = JSON.parseObject(tcp.recv());
        Boolean state = retJson.getBoolean("state");
        logger.info(String.format("[%s]: setPatternFile(%s) -> %s", appName, patternFile, state));
        return state;
    }

    public boolean setBfuncFile(String bfuncFile) {
        String content = Util.readFileContent(bfuncFile);

        JSONObject jo = new JSONObject(3);
        jo.put("api", "set_bfunc_file");
        jo.put("app_name", appName);
        jo.put("file_name", Util.getSimpleFileName(bfuncFile));
        jo.put("content", content);
        tcp.send(jo.toJSONString());
        JSONObject retJson = JSON.parseObject(tcp.recv());
        Boolean state = retJson.getBoolean("state");
        logger.info(String.format("[%s]: setBfuncFile(%s) -> %s", appName, bfuncFile, state));
        return state;
    }

    public boolean setMfuncFile(String mfuncFile) {
        String content = Util.readFileContent(mfuncFile);

        JSONObject jo = new JSONObject(3);
        jo.put("api", "set_mfunc_file");
        jo.put("app_name", appName);
        jo.put("file_name", Util.getSimpleFileName(mfuncFile));
        jo.put("content", content);
        tcp.send(jo.toJSONString());
        JSONObject retJson = JSON.parseObject(tcp.recv());
        Boolean state = retJson.getBoolean("state");
        logger.info(String.format("[%s]: setMfuncFile(%s) -> %s", appName, mfuncFile, state));
        return state;
    }

    public boolean setRfuncFile(String rfuncFile) {
        String content = Util.readFileContent(rfuncFile);

        JSONObject jo = new JSONObject(3);
        jo.put("api", "set_rfunc_file");
        jo.put("app_name", appName);
        jo.put("file_name", Util.getSimpleFileName(rfuncFile));
        jo.put("content", content);
        tcp.send(jo.toJSONString());
        JSONObject retJson = JSON.parseObject(tcp.recv());
        Boolean state = retJson.getBoolean("state");
        logger.info(String.format("[%s]: setRfuncFile(%s) -> %s", appName, rfuncFile, state));
        return state;
    }

    public boolean setCtxValidator(String ctxValidator) {
        JSONObject jo = new JSONObject(3);
        jo.put("api", "set_ctx_validator");
        jo.put("app_name", appName);
        jo.put("ctx_validator", ctxValidator);
        tcp.send(jo.toJSONString());
        JSONObject retJson = JSON.parseObject(tcp.recv());
        Boolean state = retJson.getBoolean("state");
        logger.info(String.format("[%s]: setCtxValidator(%s) -> %s", appName, ctxValidator, state));
        return state;
    }

    //inv:
    //see CheckObject
    //TODO: 所有CheckObject.XXX 底层得改成网络通信形式而不是直接的函数调用
}
