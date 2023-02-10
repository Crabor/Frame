package app;

import app.struct.ActuatorInfo;
import app.struct.SensorInfo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import common.struct.SensorModeType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import common.socket.TCP;
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
    private final Map<String, GetValueThread> valueThreads = new HashMap<>();
    private volatile boolean getMsgFlag = false;

    public AbstractApp() {
        appName = getClass().getName();
    }

    //API:

    //app:
    public class GetValueThread extends Thread {
        private volatile boolean shouldStop = false;
        private volatile boolean stopped = true;
        private final String sensorName;
        private volatile int freq;
//        private final int udpPort;

        public GetValueThread(String sensorName, int freq) {
            this.sensorName = sensorName;
            this.freq = freq;
        }

        public void setFreq(int freq) {
            this.freq = freq;
        }

        public void run() {
            stopped = false;
            while (!shouldStop) {
                try {
                    Thread.sleep(1000 / freq);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (getMsgFlag) {
                    String value = getSensorData(sensorName);
                    getMsg(sensorName, value);
                }

//                JSONObject sensorJson = JSON.parseObject(UDP.recv(udpPort));
//                if (sensorJson != null) {
//                    String channel = sensorJson.getString("channel");
//                    String msg = sensorJson.getString("msg");
//                    getMsg(channel, msg);
//                }
            }
            stopped = true;
        }

        public void stopThread() {
            shouldStop = true;
            while (!stopped) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public boolean connect(String ip, int port) {
        JSONObject jo = new JSONObject(4);
        jo.put("api", "connect");
        jo.put("app_name", appName);
        boolean state = false;
        try {
            tcp = new TCP(new Socket(ip, port));
            tcp.send(jo.toJSONString());

            JSONObject retJson = JSON.parseObject(tcp.recv());
            state = retJson.getBooleanValue("state");
//            int udpPort = retJson.getIntValue("udp_port");
//            getValueThread = new GetValueThread();
//            getValueThread.start();
        } catch (IOException e) {
            e.printStackTrace();
            tcp.close();
        }
        logger.info(String.format("[%s]: connect(%s, %d) -> %s", appName, ip, port, state));
        return state;
    }

    public boolean disconnect() {
        JSONObject jo = new JSONObject(2);
        jo.put("api", "disconnect");
        jo.put("app_name", appName);
        if (!valueThreads.isEmpty()) {
            valueThreads.forEach((sensorName, thread) -> {
                thread.stopThread();
            });
            valueThreads.clear();
        }
        tcp.send(jo.toJSONString());
        JSONObject retJson = JSON.parseObject(tcp.recv());
        tcp.close();
        Boolean state = retJson.getBoolean("state");
        logger.info(String.format("[%s]: disconnect() -> %s", appName, state));
        return state;
    }

    //sensor:
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

    public boolean getRegisteredSensorsStatus() {
        JSONObject jo = new JSONObject(2);
        jo.put("api", "get_registered_sensors_status");
        jo.put("app_name", appName);
        tcp.send(jo.toJSONString());
        JSONObject retJson = JSON.parseObject(tcp.recv());
        Boolean state = retJson.getBoolean("state");
        logger.info(String.format("[%s]: getRegisteredSensorsStatus() -> %s", appName, state));
        return state;
    }

    private int getSensorFreq(String sensorName) {
        JSONObject jo = new JSONObject(3);
        jo.put("api", "get_sensor_freq");
        jo.put("app_name", appName);
        jo.put("sensor_name", sensorName);
        tcp.send(jo.toJSONString());
        JSONObject retJson = JSON.parseObject(tcp.recv());
        int freq = retJson.getInteger("freq");
//        logger.info(String.format("[%s]: getSensorFreq(%s) -> %d", appName, sensorName, freq));
        return freq;
    }

    private boolean isSensorRegistered(String sensorName) {
        JSONObject jo = new JSONObject(3);
        jo.put("api", "is_sensor_registered");
        jo.put("app_name", appName);
        jo.put("sensor_name", sensorName);
        tcp.send(jo.toJSONString());
        JSONObject retJson = JSON.parseObject(tcp.recv());
        Boolean state = retJson.getBoolean("state");
//        logger.info(String.format("[%s]: isSensorRegistered(%s) -> %s", appName, sensorName, state));
        return state;
    }

    public boolean registerSensor(String sensorName, SensorModeType type, int freq) {
        JSONObject jo = new JSONObject(3);
        jo.put("api", "register_sensor");
        jo.put("app_name", appName);
        jo.put("sensor_name", sensorName);
        Boolean state = true;
        if (!isSensorRegistered(sensorName)) {
            tcp.send(jo.toJSONString());
            JSONObject retJson = JSON.parseObject(tcp.recv());
            state = retJson.getBoolean("state");
        }
        if (type == SensorModeType.ACTIVE) {
            if (freq != -1) {
                state = false;
            } else {
                if (valueThreads.containsKey(sensorName)) {
                    valueThreads.get(sensorName).stopThread();
                    valueThreads.remove(sensorName);
                }
            }
        } else {
            if (freq == -1 || freq > 0) {
                if (freq == -1) {
                    freq = getSensorFreq(sensorName);
                }
                if (valueThreads.containsKey(sensorName)) {
                    valueThreads.get(sensorName).stopThread();
                    valueThreads.remove(sensorName);
                }
                GetValueThread thread = new GetValueThread(sensorName, freq);
                valueThreads.put(sensorName, thread);
                thread.start();
            } else {
                state = false;
            }
        }
        logger.info(String.format("[%s]: registerSensor(%s, %s, %d) -> %s", appName, sensorName, type, freq, state));
        return state;
    }

    public boolean registerSensor(String sensorName, SensorModeType type) {
        return registerSensor(sensorName, type, -1);
    }

    public boolean registerSensor(String sensorName) {
        return registerSensor(sensorName, SensorModeType.ACTIVE);
    }

    public boolean cancelSensor(String sensorName) {
        JSONObject jo = new JSONObject(3);
        jo.put("api", "cancel_sensor");
        jo.put("app_name", appName);
        jo.put("sensor_name", sensorName);
        if (valueThreads.containsKey(sensorName)) {
            valueThreads.get(sensorName).stopThread();
            valueThreads.remove(sensorName);
        }
        tcp.send(jo.toJSONString());
        JSONObject retJson = JSON.parseObject(tcp.recv());
        Boolean state = retJson.getBoolean("state");
        logger.info(String.format("[%s]: cancelSensor(%s) -> %s", appName, sensorName, state));
        return state;
    }

    public boolean cancelAllSensors() {
        JSONObject jo = new JSONObject(2);
        jo.put("api", "cancel_all_sensors");
        jo.put("app_name", appName);
        if (!valueThreads.isEmpty()) {
            valueThreads.forEach((sensorName, thread) -> {
                thread.stopThread();
            });
            valueThreads.clear();
        }
        tcp.send(jo.toJSONString());
        JSONObject retJson = JSON.parseObject(tcp.recv());
        Boolean state = retJson.getBoolean("state");
        logger.info(String.format("[%s]: cancelAllSensors() -> %s", appName, state));
        return state;
    }

    public String getSensorData(String sensorName) {
        JSONObject jo = new JSONObject(3);
        jo.put("api", "get_sensor_data");
        jo.put("app_name", appName);
        jo.put("sensor_name", sensorName);
        tcp.send(jo.toJSONString());
        JSONObject retJson = JSON.parseObject(tcp.recv());
        String value = retJson.getString("value");
        if (!Thread.currentThread().getStackTrace()[2].getClassName().contains("GetValueThread")) {
            logger.info(String.format("[%s]: getSensorData(%s) -> %s", appName, sensorName, value));
        }
        return value;
    }

    public Map<String, String> getAllSensorData() {
        Map<String, String> ret = new HashMap<>();
        JSONObject jo = new JSONObject(2);
        jo.put("api", "get_all_sensor_data");
        jo.put("app_name", appName);
        tcp.send(jo.toJSONString());
        JSONArray ja = JSON.parseArray(tcp.recv());
        for (Object obj : ja) {
            JSONObject joo = (JSONObject) obj;
            ret.put(joo.getString("sensor_name"), joo.getString("value"));
        }
        logger.info(String.format("[%s]: getAllSensorData() -> %s", appName, ret));
        return ret;
    }

    public boolean getMsgThread(CmdType type) {
        if (type == CmdType.START) {
            getMsgFlag = true;
        } else if (type == CmdType.STOP) {
            getMsgFlag = false;
        } else {
            logger.info(String.format("[%s]: getMsgThread(%s) -> false", appName, type));
            return false;
        }
        logger.info(String.format("[%s]: getMsgThread(%s) -> true", appName, type));
        return true;
    }

    //actuator:
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
        jo.put("api", "get_registered_actuators");
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

    public boolean getRegisteredActuatorStatus() {
        JSONObject jo = new JSONObject(2);
        jo.put("api", "get_registered_actuators_status");
        jo.put("app_name", appName);
        tcp.send(jo.toJSONString());
        JSONObject retJson = JSON.parseObject(tcp.recv());
        Boolean state = retJson.getBoolean("state");
        logger.info(String.format("[%s]: getRegisteredActuatorStatus() -> %s", appName, state));
        return state;
    }

    public boolean registerActuator(String actuatorName) {
        JSONObject jo = new JSONObject(3);
        jo.put("api", "register_actuator");
        jo.put("app_name", appName);
        jo.put("actuator_name", actuatorName);
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
        jo.put("actuator_name", actuatorName);
        tcp.send(jo.toJSONString());
        JSONObject retJson = JSON.parseObject(tcp.recv());
        Boolean state = retJson.getBoolean("state");
        logger.info(String.format("[%s]: cancelActuator(%s) -> %s", appName, actuatorName, state));
        return state;
    }

    public boolean cancelAllActuators() {
        JSONObject jo = new JSONObject(2);
        jo.put("api", "cancel_all_actuators");
        jo.put("app_name", appName);
        tcp.send(jo.toJSONString());
        JSONObject retJson = JSON.parseObject(tcp.recv());
        Boolean state = retJson.getBoolean("state");
        logger.info(String.format("[%s]: cancelAllActuators() -> %s", appName, state));
        return state;
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
        String javaFile = bfuncFile.replace(".class", ".java");
        String content = Util.readFileContent(javaFile);

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
        String javaFile = mfuncFile.replace(".class", ".java");
        String content = Util.readFileContent(javaFile);

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
