package app;

import app.struct.ActorInfo;
import app.struct.SensorInfo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import common.socket.TCP;
import common.struct.*;
import common.struct.enumeration.CmdType;
import common.struct.enumeration.SensorMode;
import common.struct.enumeration.ServiceType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RemoteConnector {
    //单例模式
    private static RemoteConnector instance;
    private TCP tcp = null;
    private Log logger = LogFactory.getLog(RemoteConnector.class);
    private AbstractApp app = null;

    private RemoteConnector() {}

    public static RemoteConnector getInstance() {
        if (instance == null) {
            synchronized (RemoteConnector.class) {
                if (instance == null) {
                    instance = new RemoteConnector();
                }
            }
        }
        return instance;
    }

    public boolean connectPlatform(String ip, int port) {
        JSONObject jo = new JSONObject(1);
        jo.put("api", "connect");
        boolean state = false;
        try {
            tcp = new TCP(new Socket(ip, port));
            tcp.send(jo.toJSONString());

            JSONObject retJson = JSON.parseObject(tcp.recv());
            state = retJson.getBooleanValue("state");
        } catch (IOException e) {
            e.printStackTrace();
            tcp.close();
        }
        logger.info(String.format("[PlatformConnector]: connectPlatform(%s, %d) -> %s", ip, port, state));
        return state;
    }

    public boolean disConnectPlatform() {
        JSONObject jo = new JSONObject(1);
        jo.put("api", "disconnect");
        tcp.send(jo.toJSONString());
        JSONObject retJson = JSON.parseObject(tcp.recv());
        boolean state = retJson.getBooleanValue("state");
        logger.info(String.format("[PlatformConnector]: disConnectPlatform() -> %s", state));
        if (state && app != null) {
            app.stopGetValueThread();
            app = null;
        }
        return state;
    }

    public boolean checkConnected() {
        JSONObject jo = new JSONObject(1);
        jo.put("api", "is_connected");
        tcp.send(jo.toJSONString());
        JSONObject retJson = JSON.parseObject(tcp.recv());
        boolean state = retJson.getBooleanValue("state");
        logger.info(String.format("[PlatformConnector]: checkConnected() -> %s", state));
        return state;
    }

    public boolean registerApp(AbstractApp app) {
        JSONObject jo = new JSONObject(2);
        jo.put("api", "register_app");
        jo.put("app_name", app.appName);
        tcp.send(jo.toJSONString());
        JSONObject retJson = JSON.parseObject(tcp.recv());
        boolean state = retJson.getBooleanValue("state");
        if (state) {
            this.app = app;
            int port = retJson.getIntValue("udp_port");
            app.startGetValueThread(port);
        }
        logger.info(String.format("[PlatformConnector]: registerApp(%s) -> %s", app.appName, state));
        return state;
    }

    public boolean unregisterApp(AbstractApp app) {
        JSONObject jo = new JSONObject(2);
        jo.put("api", "unregister_app");
        jo.put("app_name", app.appName);
        tcp.send(jo.toJSONString());
        JSONObject retJson = JSON.parseObject(tcp.recv());
        boolean state = retJson.getBooleanValue("state");
        if (state) {
            this.app.stopGetValueThread();
            this.app = null;
        }
        logger.info(String.format("[PlatformConnector]: unregisterApp(%s) -> %s", app.appName, state));
        return state;
    }

    public Map<String, SensorInfo> getSupportedSensors() {
        JSONObject jo = new JSONObject(1);
        jo.put("api", "get_supported_sensors");
        tcp.send(jo.toJSONString());
        JSONArray retJson = JSON.parseArray(tcp.recv());

        Map<String, SensorInfo> ret = new HashMap<>();
        retJson.forEach(obj -> {
            JSONObject joo = (JSONObject) obj;
            ret.put(joo.getString("sensor_name"), new SensorInfo(joo));
        });
        logger.info(String.format("[%s]: getSupportedSensors() -> %s", app.appName, ret));
        return ret;
    }

    public Map<String, SensorInfo> getRegisteredSensors() {
        JSONObject jo = new JSONObject(1);
        jo.put("api", "get_registered_sensors");
        tcp.send(jo.toJSONString());
        JSONArray retJson = JSON.parseArray(tcp.recv());

        Map<String, SensorInfo> ret = new HashMap<>();
        retJson.forEach(obj -> {
            JSONObject joo = (JSONObject) obj;
            ret.put(joo.getString("sensor_name"), new SensorInfo(joo));
        });
        logger.info(String.format("[%s]: getRegisteredSensors() -> %s", app.appName, ret));
        return ret;
    }

    public boolean getRegisteredSensorsStatus() {
        JSONObject jo = new JSONObject(2);
        jo.put("api", "get_registered_sensors_status");
        tcp.send(jo.toJSONString());
        JSONObject retJson = JSON.parseObject(tcp.recv());
        Boolean state = retJson.getBoolean("state");
        logger.info(String.format("[%s]: getRegisteredSensorsStatus() -> %s", app.appName, state));
        return state;
    }

    public boolean registerSensor(String sensorName, SensorMode mode, int freq) {
        JSONObject jo = new JSONObject(4);
        jo.put("api", "register_sensor");
        jo.put("sensor_name", sensorName);
        jo.put("mode", mode);
        jo.put("freq", freq);
        tcp.send(jo.toJSONString());
        JSONObject retJson = JSON.parseObject(tcp.recv());
        Boolean state = retJson.getBoolean("state");
        logger.info(String.format("[%s]: registerSensor(%s,%s,%d) -> %s", app.appName, sensorName, mode, freq, state));
        return state;
    }

    public boolean cancelSensor(String sensorName) {
        JSONObject jo = new JSONObject(2);
        jo.put("api", "cancel_sensor");
        jo.put("sensor_name", sensorName);
        tcp.send(jo.toJSONString());
        JSONObject retJson = JSON.parseObject(tcp.recv());
        Boolean state = retJson.getBoolean("state");
        logger.info(String.format("[%s]: cancelSensor(%s) -> %s", app.appName, sensorName, state));
        return state;
    }

    public boolean cancelAllSensors() {
        JSONObject jo = new JSONObject(1);
        jo.put("api", "cancel_all_sensors");
        tcp.send(jo.toJSONString());
        JSONObject retJson = JSON.parseObject(tcp.recv());
        Boolean state = retJson.getBoolean("state");
        logger.info(String.format("[%s]: cancelAllSensors() -> %s", app.appName, state));
        return state;
    }

    public SensorData getSensorData(String sensorName) {
        JSONObject jo = new JSONObject(2);
        jo.put("api", "get_sensor_data");
        jo.put("sensor_name", sensorName);
        tcp.send(jo.toJSONString());
        SensorData ret = SensorData.fromJSONString(tcp.recv());

        logger.info(String.format("[%s]: getSensorData(%s) -> %s", app.appName, sensorName, ret));
        return ret;
    }

    public Map<String, SensorData> getAllSensorData() {
        Map<String, SensorData> ret = new HashMap<>();
        JSONObject jo = new JSONObject(1);
        jo.put("api", "get_all_sensor_data");
        tcp.send(jo.toJSONString());
        JSONArray ja = JSON.parseArray(tcp.recv());
        for (Object obj : ja) {
            JSONObject joo = (JSONObject) obj;
            ret.put(joo.getString("sensor_name"), SensorData.fromJSONString(joo.getString("value")));
        }
        logger.info(String.format("[%s]: getAllSensorData() -> %s", app.appName, ret));
        return ret;
    }

    public boolean getMsgThread(CmdType cmd) {
        JSONObject jo = new JSONObject(2);
        jo.put("api", "get_msg_thread");
        jo.put("cmd", cmd);
        tcp.send(jo.toJSONString());
        JSONObject retJson = JSON.parseObject(tcp.recv());
        Boolean state = retJson.getBoolean("state");
        logger.info(String.format("[%s]: getMsgThreadState(%s) -> %s", app.appName, cmd, state));
        return true;
    }

    public Map<String, ActorInfo> getSupportedActors() {
        JSONObject jo = new JSONObject(1);
        jo.put("api", "get_supported_actuators");
        tcp.send(jo.toJSONString());
        JSONArray retJson = JSON.parseArray(tcp.recv());

        Map<String, ActorInfo> ret = new HashMap<>();
        retJson.forEach(obj -> {
            JSONObject joo = (JSONObject) obj;
            ret.put(joo.getString("actuator_name"), new ActorInfo(joo));
        });
        logger.info(String.format("[%s]: getSupportedActors() -> %s", app.appName, ret));
        return ret;
    }

    public Map<String, ActorInfo> getRegisteredActors() {
        JSONObject jo = new JSONObject(1);
        jo.put("api", "get_registered_actuators");
        tcp.send(jo.toJSONString());
        JSONArray retJson = JSON.parseArray(tcp.recv());

        Map<String, ActorInfo> ret = new HashMap<>();
        retJson.forEach(obj -> {
            JSONObject joo = (JSONObject) obj;
            ret.put(joo.getString("actuator_name"), new ActorInfo(joo));
        });
        logger.info(String.format("[%s]: getRegisteredActors() -> %s", app.appName, ret));
        return ret;
    }

    public boolean getRegisteredActorsStatus() {
        JSONObject jo = new JSONObject(1);
        jo.put("api", "get_registered_actuators_status");
        tcp.send(jo.toJSONString());
        JSONObject retJson = JSON.parseObject(tcp.recv());
        Boolean state = retJson.getBoolean("state");
        logger.info(String.format("[%s]: getRegisteredActorStatus() -> %s", app.appName, state));
        return state;
    }

    public boolean registerActor(String actuatorName) {
        JSONObject jo = new JSONObject(2);
        jo.put("api", "register_actuator");
        jo.put("actuator_name", actuatorName);
        tcp.send(jo.toJSONString());
        JSONObject retJson = JSON.parseObject(tcp.recv());
        Boolean state = retJson.getBoolean("state");
        logger.info(String.format("[%s]: registerActor(%s) -> %s", app.appName, actuatorName, state));
        return state;
    }

    public boolean cancelActor(String actuatorName) {
        JSONObject jo = new JSONObject(2);
        jo.put("api", "cancel_actuator");
        jo.put("actuator_name", actuatorName);
        tcp.send(jo.toJSONString());
        JSONObject retJson = JSON.parseObject(tcp.recv());
        Boolean state = retJson.getBoolean("state");
        logger.info(String.format("[%s]: cancelActor(%s) -> %s", app.appName, actuatorName, state));
        return state;
    }

    public boolean cancelAllActors() {
        JSONObject jo = new JSONObject(1);
        jo.put("api", "cancel_all_actuators");
        tcp.send(jo.toJSONString());
        JSONObject retJson = JSON.parseObject(tcp.recv());
        Boolean state = retJson.getBoolean("state");
        logger.info(String.format("[%s]: cancelAllActors() -> %s", app.appName, state));
        return state;
    }

    public boolean setActorCmd(String actuatorName, String cmd, String ... args) {
        JSONObject jo = new JSONObject(5);
        jo.put("api", "set_actuator");
        jo.put("actuator_name", actuatorName);
        jo.put("cmd_type", cmd);
        JSONArray ja = new JSONArray();
        for (String arg : args) {
            JSONObject joo = new JSONObject(1);
            joo.put("arg", arg);
            ja.add(joo);
        }
        jo.put("args", ja);
        tcp.send(jo.toJSONString());
        JSONObject retJson = JSON.parseObject(tcp.recv());

        boolean ret = retJson.getBoolean("state");
        logger.info(String.format("[%s]: setActorCmd(%s, %s, %s) -> %s", app.appName, actuatorName, cmd, Arrays.toString(args), ret));
        return ret;
    }

    public boolean isServiceOn(ServiceType service) {
        JSONObject jo = new JSONObject(2);
        jo.put("api", "is_service_on");
        jo.put("service_type", service.toString());
        tcp.send(jo.toJSONString());
        JSONObject retJson = JSON.parseObject(tcp.recv());
        Boolean state = retJson.getBoolean("state");
        logger.info(String.format("[%s]: isServerOn(%s) -> %s", app.appName, service, state));
        return state;
    }

    public boolean serviceStart(ServiceType service, ServiceConfig config) {
        JSONObject jo = new JSONObject(3);
        jo.put("api", "start_service");
        jo.put("service_type", service.toString());
        jo.put("config", config);
        tcp.send(jo.toJSONString());
        JSONObject retJson = JSON.parseObject(tcp.recv());
        Boolean state = retJson.getBoolean("state");
        logger.info(String.format("[%s]: serviceStart(%s,%s) -> %s", app.appName, service, config, state));
        return state;
    }

    public boolean serviceStop(ServiceType service) {
        JSONObject jo = new JSONObject(2);
        jo.put("api", "stop_service");
        jo.put("service_type", service.toString());
        tcp.send(jo.toJSONString());
        JSONObject retJson = JSON.parseObject(tcp.recv());
        Boolean state = retJson.getBoolean("state");
        logger.info(String.format("[%s]: serviceStop(%s) -> %s", app.appName, service, state));
        return state;
    }

    public boolean serviceCall(ServiceType service, CmdType cmd, ServiceConfig config) {
        JSONObject jo = new JSONObject(4);
        jo.put("api", "service_call");
        jo.put("service_type", service.toString());
        jo.put("cmd_type", cmd.toString());
        jo.put("config", config);
        tcp.send(jo.toJSONString());
        JSONObject retJson = JSON.parseObject(tcp.recv());

        boolean ret = retJson.getBoolean("state");
        logger.info(String.format("[%s]: serviceCall(%s, %s, %s) -> %s", app.appName, service, cmd, config, ret));
        return ret;
    }
}
