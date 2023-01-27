package app;

import app.struct.ActuatorInfo;
import app.struct.SensorInfo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import platform.comm.socket.UDP;
import platform.struct.CmdType;
import platform.struct.ServiceType;

import java.io.*;
import java.net.Socket;
import java.util.Map;

public abstract class AbstractApp implements App {
    private Socket socket;
    private DataOutputStream outToServer;
    private BufferedReader inFromServer;

    public AbstractApp() {
        setting();
    }

    //API:

    //app:
    public boolean registerApp(String ip, int port) {
        JSONObject jo = new JSONObject(4);
        jo.put("api", "register_app");
        jo.put("app_name", this.getClass().getName());
        boolean state = false;
        try {
            socket = new Socket(ip, port);
            DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outToServer.writeBytes(jo.toJSONString() + '\n');

            JSONObject retJson = JSON.parseObject(inFromServer.readLine());
            state = retJson.getBooleanValue("state");
            int udpPort = retJson.getIntValue("udp_port");
            new Thread(() -> {
                while (true) {
                    JSONObject sensorJson = JSON.parseObject(UDP.recv(udpPort));
                    String channel = sensorJson.getString("channel");
                    String msg = sensorJson.getString("msg");
                    getMsg(channel, msg);
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return state;
    }

    public boolean cancelApp() {
        return false;
    }

    //sensor:
    public boolean registerSensor(String sensorName) {
        return false;
    }

    public boolean cancelSensor(String sensorName) {
        return false;
    }

    public Map<String, SensorInfo> getRegisteredSensors() {
        return null;
    }

    public Map<String, SensorInfo> getSupportedSensors() {
        return null;
    }

    //actuator:
    public boolean registerActuator(String actuatorName) {
        return false;
    }

    public boolean cancelActuator(String actuatorName) {
        return false;
    }

    public Map<String, ActuatorInfo> getSupportedActuators() {
        return null;
    }

    public Map<String, ActuatorInfo> getRegisteredActuators() {
        return null;
    }

    public boolean setActuator(String actuatorName, String action) {
        return false;
    }

    //service:
    public boolean isServerOn(ServiceType type) {
        return false;
    }

    //TODO: 由于改成进程间通信（网络套接字）而不是进程内部函数调用，所以参数返回值不能为Object
    public String call(ServiceType serviceType, CmdType cmdType, String... args) {
        return null;
    }

    //ctx:
    //TODO:假如应用和平台是处于不同的主机，那么ruleFile是存在平台所在主机还是存在应用所在主机？
    // 如果存在应用所在主机的话，那么以下命令将会将对应文件内容全部加载到字符串然后通过网络发送到平台，网络通信压力？
    public boolean setRuleFile(String ruleFile) {
        return false;
    }

    public boolean setPatternFile(String patternFile) {
        return false;
    }

    public boolean setBfuncFile(String bfuncFile) {
        return false;
    }

    public boolean setMfuncFile(String mfuncFile) {
        return false;
    }

    public boolean setRfuncFile(String rfuncFile) {
        return false;
    }

    public boolean setCtxValidator(String ctxValidator) {
        return false;
    }

    //inv:
    //see CheckObject
    //TODO: 所有CheckObject.XXX 底层得改成网络通信形式而不是直接的函数调用
}
