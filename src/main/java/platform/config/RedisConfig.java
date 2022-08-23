package platform.config;

import com.alibaba.fastjson.JSONObject;

public class RedisConfig {
    private String serverAddress;
    private int serverPort;

    public RedisConfig(JSONObject object) {
        try {
            this.serverAddress = object.getString("serverAddress");
        } catch (NullPointerException e) {
            this.serverAddress = "127.0.0.1";
        }

        try {
            this.serverPort = object.getInteger("serverPort");
        } catch (NullPointerException e) {
            this.serverPort = 6379;
        }
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public int getServerPort() {
        return serverPort;
    }

    @Override
    public String toString() {
        return "RedisConfig{" +
                "serverAddress='" + serverAddress + '\'' +
                ", serverPort=" + serverPort +
                '}';
    }
}
