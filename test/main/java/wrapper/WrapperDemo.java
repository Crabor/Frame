package wrapper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import common.socket.CmdMessage;
import common.util.Util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class WrapperDemo {
    public static void main(String[] args) throws IOException {
        String config = Util.readFileContent("Resources/config/wrapper/config.json");
        WrapperRemoteConnector connector = WrapperRemoteConnector.getInstance();
        if (connector.register("127.0.0.1", 9091, config)) {
            while (true) {
                CmdMessage msg = connector.recv();
                switch (msg.cmd) {
                    case "sensory_request":
                        JSONObject value = new JSONObject();
                        value.put("speed", 10.0);
                        value.put("longitude", 20.0);
                        value.put("latitude", 30.0);
                        CmdMessage response = new CmdMessage("sensory_back", value.toJSONString());
                        connector.send(response.toString());
                        connector.getLogger().info("[Wrapper]: " + response);
                        break;
                }
            }
//            connector.close();
        }
    }
}