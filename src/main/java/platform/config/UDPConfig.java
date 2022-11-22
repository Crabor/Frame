package platform.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UDPConfig {
    private final int serverPort;
    private final int clientPort;

    public UDPConfig(JSONObject object){
        this.serverPort = object.getIntValue("serverPort");
        this.clientPort = object.getIntValue("clientPort");
    }

    public int getServerPort() {
        return serverPort;
    }

    public int getClientPort() {
        return clientPort;
    }

    @Override
    public String toString() {
        return "UDPConfig{" +
                "serverPort=" + serverPort +
                ", clientPort=" + clientPort +
                '}';
    }
}
