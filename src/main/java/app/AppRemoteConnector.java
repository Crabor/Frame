package app;

import app.struct.ActorInfo;
import app.struct.SensorInfo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import common.socket.AbstractTCP;
import common.socket.TCP;
import common.struct.*;
import common.struct.enumeration.CmdType;
import common.struct.enumeration.SensorMode;
import common.struct.enumeration.ServiceType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class AppRemoteConnector {
    //单例模式
    private static AppRemoteConnector instance;
    private TCP tcp = null;
    private int udpPort = -1;
    private Log logger = LogFactory.getLog(AppRemoteConnector.class);
    private AbstractApp app = null;

    private AppRemoteConnector() {}

    public static AppRemoteConnector getInstance() {
        if (instance == null) {
            synchronized (AppRemoteConnector.class) {
                if (instance == null) {
                    instance = new AppRemoteConnector();
                }
            }
        }
        return instance;
    }

    public class AppRemoteConnectorTCP extends AbstractTCP {
        public AppRemoteConnectorTCP(Socket socket, boolean lockFlag) {
            super(socket, lockFlag);
        }

        public AppRemoteConnectorTCP(Socket socket) {
            super(socket);
        }

        @Override
        public void close() {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (app != null) {
                app.stopGetValueThread();
//                app = null;
            }
        }

        @Override
        public void callback() {
            logger.info("[AppConnector]: TCP connection is broken.");
            if (app != null) {
                app.stopGetValueThread();
//                app = null;
            }
        }
    }

    public TCP getTCP() {
        return tcp;
    }

    public String getAppName() {
        return app.appName;
    }

    public boolean connectPlatform(String ip, int port) {
        JSONObject jo = new JSONObject(1);
        jo.put("api", "connect");
        boolean state = false;
        try {
            tcp = new AppRemoteConnectorTCP(new Socket(ip, port));
            tcp.send(jo.toJSONString());

            String recv = tcp.recv();
            if (recv != null) {
                JSONObject retJson = JSON.parseObject(recv);
                state = retJson.getBooleanValue("state");
            }
        } catch (IOException e) {
            e.printStackTrace();
            tcp.close();
        }
        logger.info(String.format("[AppConnector]: connectPlatform(%s, %d) -> %s", ip, port, state));
        return state;
    }

    public boolean disConnectPlatform() {
        JSONObject jo = new JSONObject(1);
        jo.put("api", "disconnect");
        tcp.send(jo.toJSONString());

        boolean state = false;
        String recv = tcp.recv();
        if (recv != null) {
            JSONObject retJson = JSON.parseObject(recv);
            state = retJson.getBooleanValue("state");
        }
        logger.info(String.format("[AppConnector]: disConnectPlatform() -> %s", state));
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

        boolean state = false;
        String recv = tcp.recv();
        if (recv != null) {
            JSONObject retJson = JSON.parseObject(recv);
            state = retJson.getBooleanValue("state");
        }
        logger.info(String.format("[AppConnector]: checkConnected() -> %s", state));
        return state;
    }

    public boolean registerApp(AbstractApp app) {
        JSONObject jo = new JSONObject(2);
        jo.put("api", "register_app");
        jo.put("app_name", app.appName);
        tcp.send(jo.toJSONString());

        boolean state = false;
        String recv = tcp.recv();
        if (recv != null) {
            JSONObject retJson = JSON.parseObject(recv);
            state = retJson.getBooleanValue("state");
            if (state) {
                this.app = app;
                udpPort = retJson.getIntValue("udp_port");
            }
        }
        logger.info(String.format("[AppConnector]: registerApp(%s) -> %s", app.appName, state));
        return state;
    }

    public boolean unregisterApp(AbstractApp app) {
        JSONObject jo = new JSONObject(2);
        jo.put("api", "unregister_app");
        jo.put("app_name", app.appName);
        tcp.send(jo.toJSONString());

        boolean state = false;
        String recv = tcp.recv();
        if (recv != null) {
            JSONObject retJson = JSON.parseObject(recv);
            state = retJson.getBooleanValue("state");
            if (state) {
                this.app.stopGetValueThread();
                this.app = null;
                udpPort = -1;
            }
        }
        logger.info(String.format("[AppConnector]: unregisterApp(%s) -> %s", app.appName, state));
        return state;
    }

    public Map<String, SensorInfo> getSupportedSensors() {
        JSONObject jo = new JSONObject(1);
        jo.put("api", "get_supported_sensors");
        tcp.send(jo.toJSONString());

        JSONArray retJson = new JSONArray();
        String recv = tcp.recv();
        if (recv != null) {
            retJson = JSON.parseArray(recv);
        }
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

        JSONArray retJson = new JSONArray();
        String recv = tcp.recv();
        if (recv != null) {
            retJson = JSON.parseArray(recv);
        }
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

        boolean state = false;
        String recv = tcp.recv();
        if (recv != null) {
            JSONObject retJson = JSON.parseObject(recv);
            state = retJson.getBooleanValue("state");
        }
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

        boolean state = false;
        String recv = tcp.recv();
        if (recv != null) {
            JSONObject retJson = JSON.parseObject(recv);
            state = retJson.getBooleanValue("state");
        }
        logger.info(String.format("[%s]: registerSensor(%s,%s,%d) -> %s", app.appName, sensorName, mode, freq, state));
        return state;
    }

    public boolean cancelSensor(String sensorName) {
        JSONObject jo = new JSONObject(2);
        jo.put("api", "cancel_sensor");
        jo.put("sensor_name", sensorName);
        tcp.send(jo.toJSONString());

        boolean state = false;
        String recv = tcp.recv();
        if (recv != null) {
            JSONObject retJson = JSON.parseObject(recv);
            state = retJson.getBooleanValue("state");
        }
        logger.info(String.format("[%s]: cancelSensor(%s) -> %s", app.appName, sensorName, state));
        return state;
    }

    public boolean cancelAllSensors() {
        JSONObject jo = new JSONObject(1);
        jo.put("api", "cancel_all_sensors");
        tcp.send(jo.toJSONString());

        boolean state = false;
        String recv = tcp.recv();
        if (recv != null) {
            JSONObject retJson = JSON.parseObject(recv);
            state = retJson.getBooleanValue("state");
        }
        logger.info(String.format("[%s]: cancelAllSensors() -> %s", app.appName, state));
        return state;
    }

    public SensorData getSensorData(String sensorName) {
        JSONObject jo = new JSONObject(2);
        jo.put("api", "get_sensor_data");
        jo.put("sensor_name", sensorName);
        tcp.send(jo.toJSONString());

        String recv = tcp.recv();
        SensorData ret = SensorData.defaultErrorData();
        if (recv != null) {
            ret = SensorData.fromJSONString(recv);
        }

        logger.info(String.format("[%s]: getSensorData(%s) -> %s", app.appName, sensorName, ret));
        return ret;
    }

    public Map<String, SensorData> getAllSensorData() {
        Map<String, SensorData> ret = new HashMap<>();
        JSONObject jo = new JSONObject(1);
        jo.put("api", "get_all_sensor_data");
        tcp.send(jo.toJSONString());

        JSONArray ja = new JSONArray();
        String recv = tcp.recv();
        if (recv != null) {
            ja = JSON.parseArray(recv);
        }
        for (Object obj : ja) {
            JSONObject joo = (JSONObject) obj;
            ret.put(joo.getString("sensor_name"), SensorData.fromJSONString(joo.getString("value")));
        }
        logger.info(String.format("[%s]: getAllSensorData() -> %s", app.appName, ret));
        return ret;
    }

    public boolean getMsgThread(CmdType cmd) {
        boolean state = false;
        if (udpPort != -1) {
            JSONObject jo = new JSONObject(2);
            jo.put("api", "get_msg_thread");
            jo.put("cmd", cmd);
            tcp.send(jo.toJSONString());

            state = false;
            String recv = tcp.recv();
            if (recv != null) {
                JSONObject retJson = JSON.parseObject(recv);
                state = retJson.getBooleanValue("state");
            }
            if (state) {
                if (cmd == CmdType.START) {
                    this.app.startGetValueThread(udpPort);
                } else if (cmd == CmdType.STOP) {
                    this.app.stopGetValueThread();
                }
            }
        }
        logger.info(String.format("[%s]: getMsgThreadState(%s) -> %s", app.appName, cmd, state));
        return state;
    }

    public Map<String, ActorInfo> getSupportedActors() {
        JSONObject jo = new JSONObject(1);
        jo.put("api", "get_supported_actors");
        tcp.send(jo.toJSONString());


        JSONArray retJson = new JSONArray();
        String recv = tcp.recv();
        if (recv != null) {
            retJson = JSON.parseArray(recv);
        }
        Map<String, ActorInfo> ret = new HashMap<>();
        retJson.forEach(obj -> {
            JSONObject joo = (JSONObject) obj;
            ret.put(joo.getString("actor_name"), new ActorInfo(joo));
        });
        logger.info(String.format("[%s]: getSupportedActors() -> %s", app.appName, ret));
        return ret;
    }

    public Map<String, ActorInfo> getRegisteredActors() {
        JSONObject jo = new JSONObject(1);
        jo.put("api", "get_registered_actors");
        tcp.send(jo.toJSONString());


        JSONArray retJson = new JSONArray();
        String recv = tcp.recv();
        if (recv != null) {
            retJson = JSON.parseArray(recv);
        }
        Map<String, ActorInfo> ret = new HashMap<>();
        retJson.forEach(obj -> {
            JSONObject joo = (JSONObject) obj;
            ret.put(joo.getString("actor_name"), new ActorInfo(joo));
        });
        logger.info(String.format("[%s]: getRegisteredActors() -> %s", app.appName, ret));
        return ret;
    }

    public boolean getRegisteredActorsStatus() {
        JSONObject jo = new JSONObject(1);
        jo.put("api", "get_registered_actors_status");
        tcp.send(jo.toJSONString());

        boolean state = false;
        String recv = tcp.recv();
        if (recv != null) {
            JSONObject retJson = JSON.parseObject(recv);
            state = retJson.getBooleanValue("state");
        }
        logger.info(String.format("[%s]: getRegisteredActorStatus() -> %s", app.appName, state));
        return state;
    }

    public boolean registerActor(String actorName) {
        JSONObject jo = new JSONObject(2);
        jo.put("api", "register_actor");
        jo.put("actor_name", actorName);
        tcp.send(jo.toJSONString());

        boolean state = false;
        String recv = tcp.recv();
        if (recv != null) {
            JSONObject retJson = JSON.parseObject(recv);
            state = retJson.getBooleanValue("state");
        }
        logger.info(String.format("[%s]: registerActor(%s) -> %s", app.appName, actorName, state));
        return state;
    }

    public boolean cancelActor(String actorName) {
        JSONObject jo = new JSONObject(2);
        jo.put("api", "cancel_actor");
        jo.put("actor_name", actorName);
        tcp.send(jo.toJSONString());

        boolean state = false;
        String recv = tcp.recv();
        if (recv != null) {
            JSONObject retJson = JSON.parseObject(recv);
            state = retJson.getBooleanValue("state");
        }
        logger.info(String.format("[%s]: cancelActor(%s) -> %s", app.appName, actorName, state));
        return state;
    }

    public boolean cancelAllActors() {
        JSONObject jo = new JSONObject(1);
        jo.put("api", "cancel_all_actors");
        tcp.send(jo.toJSONString());

        boolean state = false;
        String recv = tcp.recv();
        if (recv != null) {
            JSONObject retJson = JSON.parseObject(recv);
            state = retJson.getBooleanValue("state");
        }
        logger.info(String.format("[%s]: cancelAllActors() -> %s", app.appName, state));
        return state;
    }

//    public boolean setActorCmd(String actorName, String cmd, String ... args) {
//        JSONObject jo = new JSONObject(5);
//        jo.put("api", "set_actor");
//        jo.put("actor_name", actorName);
//        jo.put("cmd_type", cmd);
//        JSONArray ja = new JSONArray();
//        for (String arg : args) {
//            JSONObject joo = new JSONObject(1);
//            joo.put("arg", arg);
//            ja.add(joo);
//        }
//        jo.put("args", ja);
//        tcp.send(jo.toJSONString());
//        JSONObject retJson = JSON.parseObject(tcp.recv());
//
//        boolean ret = retJson.getBoolean("state");
//        logger.info(String.format("[%s]: setActorCmd(%s, %s, %s) -> %s", app.appName, actorName, cmd, Arrays.toString(args), ret));
//        return ret;
//    }

    public boolean setActorCmd(String actorName, String action) {
        JSONObject jo = new JSONObject(3);
        jo.put("api", "set_actor_cmd");
        jo.put("actor_name", actorName);
        jo.put("action", action);
        tcp.send(jo.toJSONString());

        boolean state = false;
        String recv = tcp.recv();
        if (recv != null) {
            JSONObject retJson = JSON.parseObject(recv);
            state = retJson.getBooleanValue("state");
        }
        logger.info(String.format("[%s]: setActorCmd(%s, %s) -> %s", app.appName, actorName, action, state));
        return state;
    }

    public boolean isServiceOn(ServiceType service) {
        JSONObject jo = new JSONObject(2);
        jo.put("api", "is_service_on");
        jo.put("service_type", service.toString());
        tcp.send(jo.toJSONString());

        boolean state = false;
        String recv = tcp.recv();
        if (recv != null) {
            JSONObject retJson = JSON.parseObject(recv);
            state = retJson.getBooleanValue("state");
        }
        logger.info(String.format("[%s]: isServerOn(%s) -> %s", app.appName, service, state));
        return state;
    }

    public boolean serviceStart(ServiceType service, ServiceConfig config) {
        if (service == ServiceType.INV || service == ServiceType.ALL) {
            InvCheck invCheck = InvCheck.getInstance();
            invCheck.connector = this;
        }

        JSONObject jo = new JSONObject(3);
        jo.put("api", "start_service");
        jo.put("service_type", service.toString());
        jo.put("config", config.toJSONString());
        tcp.send(jo.toJSONString());

        boolean state = false;
        String recv = tcp.recv();
        if (recv != null) {
            JSONObject retJson = JSON.parseObject(recv);
            state = retJson.getBooleanValue("state");
        }
        logger.info(String.format("[%s]: serviceStart(%s,%s) -> %s", app.appName, service, config, state));
        return state;
    }

    public boolean serviceStop(ServiceType service) {
        if (service == ServiceType.INV || service == ServiceType.ALL) {
            InvCheck invCheck = InvCheck.getInstance();
            invCheck.connector = null;
        }

        JSONObject jo = new JSONObject(2);
        jo.put("api", "stop_service");
        jo.put("service_type", service.toString());
        tcp.send(jo.toJSONString());

        boolean state = false;
        String recv = tcp.recv();
        if (recv != null) {
            JSONObject retJson = JSON.parseObject(recv);
            state = retJson.getBooleanValue("state");
        }
        logger.info(String.format("[%s]: serviceStop(%s) -> %s", app.appName, service, state));
        return state;
    }

    public boolean serviceCall(ServiceType service, CmdType cmd, ServiceConfig config) {
        if (service == ServiceType.INV || service == ServiceType.ALL) {
            if (cmd == CmdType.START) {
                InvCheck invCheck = InvCheck.getInstance();
                invCheck.connector = this;
            } else if (cmd == CmdType.STOP) {
                InvCheck invCheck = InvCheck.getInstance();
                invCheck.connector = null;
            }
        }

        JSONObject jo = new JSONObject(4);
        jo.put("api", "service_call");
        jo.put("service_type", service.toString());
        jo.put("cmd_type", cmd.toString());
        jo.put("config", config.toJSONString());
        tcp.send(jo.toJSONString());

        boolean state = false;
        String recv = tcp.recv();
        if (recv != null) {
            JSONObject retJson = JSON.parseObject(recv);
            state = retJson.getBooleanValue("state");
        }
        logger.info(String.format("[%s]: serviceCall(%s, %s, %s) -> %s", app.appName, service, cmd, config, state));
        return state;
    }
}
