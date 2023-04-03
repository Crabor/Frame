package wrapper;

import com.alibaba.fastjson.JSONObject;
import common.socket.CmdMessage;
import common.util.Util;

import java.io.IOException;

public class WrapperDemo2 {
    public static void main(String[] args) throws IOException {
        String config = Util.readFileContent("Resources/config/wrapper/config.json");
        WrapperRemoteConnector connector = WrapperRemoteConnector.getInstance();
        if (connector.register("127.0.0.1", 9091, config)) {
            int i = 0;
            boolean flag = true;
            while (true) {
                flag = (i++ % 20 == 0) != flag;
                CmdMessage msg = connector.recv();
                switch (msg.cmd) {
                    case "sensory_request":
                        JSONObject value = new JSONObject();
                        if (flag) {
                            value.put("speed", 10.0);
                            value.put("longitude", 20.0);
                            value.put("latitude", 30.0);
                        } else {
                            value.put("speed", 20.0);
                            value.put("longitude", 30.0);
                            value.put("latitude", 10.0);
                        }
                        CmdMessage response = new CmdMessage("sensory_back", value.toJSONString());
                        connector.send(response.toString());
                        System.out.println("[Wrapper]: " + response);
                        break;
                }
            }
//            connector.close();
        }
    }
}