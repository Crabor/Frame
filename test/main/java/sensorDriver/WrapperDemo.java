package sensorDriver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class WrapperDemo {
    public static void main(String[] args) throws IOException {
        String config = "{\"ConnectConfig\":{\"PlatformIP\":\"127.0.0.1\",\"PlatformPort\":8080},\"DeviceInfo\":{\"Name\":\"Car_1\",\"Type\":\"Sensor\",\"Fields\":{\"fieldName1\":\"Speed\",\"fieldName2\":\"Longitude\"}}}";

        JSONObject json = JSONObject.parseObject(config);
        JSONObject connectConfig = json.getJSONObject("ConnectConfig");
        String platformIP = connectConfig.getString("PlatformIP");
        int platformPort = connectConfig.getIntValue("PlatformPort");
        JSONObject registerCmd = new JSONObject();
        registerCmd.put("cmd", "register");
        registerCmd.put("config", config);

        Socket socket = new Socket(platformIP, platformPort);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        out.writeBytes(registerCmd.toJSONString() + '\n');
        boolean succeed = JSON.parseObject(in.readLine()).getBooleanValue("state");

        if (succeed) {
            while (true) {
                JSONObject recv = JSON.parseObject(in.readLine());
                String retJson = "";
                if (recv.getString("cmd").equalsIgnoreCase("sensor_get")) {
                    //TODO: get data from sensor
                    retJson = "{\"Speed\": 100, \"Longitude\": 120}";
                } else if (recv.getString("cmd").equalsIgnoreCase("actor_set")) {
                    String action = recv.getString("action");
                    //TODO: set data to actor
                    retJson = "{\"state\": true}";
                }
                out.writeBytes(retJson + '\n');
            }
        }
    }
}