package platform.config;

import com.alibaba.fastjson.JSONObject;

public class UDPConfig {
    private final int serverPort;
    private final String clientAddress;
    private final int clientPort;

    public UDPConfig(JSONObject object){
        this.serverPort = object.getInteger("serverPort");
        this.clientAddress = object.getString("clientAddress");
        this.clientPort = object.getInteger("clientPort");
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

    @Override
    public String toString() {
        return "UDPConfig{" +
                "serverPort=" + serverPort +
                ", clientAddress='" + clientAddress + '\'' +
                ", clientPort=" + clientPort +
                '}';
    }
}
