package platform.config;

import com.alibaba.fastjson.JSONObject;

public class TCPConfig {
    private final int appListenPort;
    private final int deviceListenPort;

    public TCPConfig(JSONObject object){
        appListenPort = object.getIntValue("appListenPort");
        deviceListenPort = object.getIntValue("deviceListenPort");
    }

    public int getAppListenPort() {
        return appListenPort;
    }

    public int getDeviceListenPort() {
        return deviceListenPort;
    }

    @Override
    public String toString() {
        return "TCPConfig{" +
                "appListenPort=" + appListenPort +
                ", deviceListenPort=" + deviceListenPort +
                '}';
    }
}
