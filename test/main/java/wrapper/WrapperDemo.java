package wrapper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import common.socket.CmdMessage;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class WrapperDemo {
    public static void main(String[] args) throws IOException {
        String config = "{\"name\":\"YellowCar\"," +
                "\"deviceType\":\"Sensor\"," +
                "\"fields\":[\"speed\",\"longitude\",\"timestamp\"]}";
        WrapperRemoteConnector connector = WrapperRemoteConnector.getInstance();
        if (connector.register("127.0.0.1", 9091, config)) {
            while (true) {
                CmdMessage msg = connector.recv();
                switch (msg.cmd) {
                    case "sensory_request":
                        CmdMessage response = new CmdMessage("sensory_back",
                                        "{\"speed\":10,\"longitude\":20," +
                                                "\"timestamp\":2745956482}");
                        connector.send(response.toString());
                        System.out.println("[Wrapper]: " + response);
                        break;
                }

            }
//            connector.close();
        }
    }
}