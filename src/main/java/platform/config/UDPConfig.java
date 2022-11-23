package platform.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UDPConfig {
    private final int serverPort;
    private final List<Integer> clientPort = new ArrayList<>();

    public UDPConfig(JSONObject object){
        this.serverPort = object.getIntValue("serverPort");
        JSONArray clientObj = (JSONArray) object.get("clientPort");
        for (Object port : clientObj) {
            JSONObject temp = (JSONObject) port;
            this.clientPort.add(temp.getIntValue("port"));
        }
    }

    public int getServerPort() {
        return serverPort;
    }

    public List<Integer> getClientPort() {
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
