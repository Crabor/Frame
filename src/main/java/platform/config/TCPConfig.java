package platform.config;

import com.alibaba.fastjson.JSONObject;

public class TCPConfig {
    private final int serverPort;

    public TCPConfig(JSONObject object){
        this.serverPort = object.getIntValue("serverPort");
    }

    public int getServerPort() {
        return serverPort;
    }

    @Override
    public String toString() {
        return "TCPConfig{" +
                "serverPort=" + serverPort +
                '}';
    }
}
