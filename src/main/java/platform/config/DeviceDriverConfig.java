package platform.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DeviceDriverConfig {
    public int serverPort;
    public String clientAddress;
    public int clientPort;
    public List<SubConfig> subConfigs = new ArrayList<>();

    public DeviceDriverConfig(JSONObject object) {
        this.serverPort = object.getInteger("serverPort");
        this.clientAddress = object.getString("clientAddress");
        this.clientPort = object.getInteger("clientPort");
        JSONArray subs = object.getJSONArray("subscribe");
        for (int i = 0; i < subs.size(); i++) {
            JSONObject sub = subs.getJSONObject(i);
            subConfigs.add(new SubConfig(sub));
        }
    }

    public int getServerPort() {
        return serverPort;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public int getClientPort() {
        return clientPort;
    }

    public List<SubConfig> getSubConfigs() {
        return subConfigs;
    }

    @Override
    public String toString() {
        return "DeviceDriverConfig{" +
                "serverPort=" + serverPort +
                ", clientAddress='" + clientAddress + '\'' +
                ", clientPort=" + clientPort +
                ", subConfigs=" + subConfigs +
                '}';
    }
}
