package wrapper;

import com.alibaba.fastjson.JSONObject;
import common.socket.CmdMessage;
import common.util.Util;

import java.io.IOException;

public class WrapperDemo3 {
    public static void main(String[] args) throws IOException {
        String config = Util.readFileContent("Resources/config/wrapper/actor_greencar.json");
        WrapperRemoteConnector connector = WrapperRemoteConnector.getInstance();
        if (connector.register("127.0.0.1", 9091, config)) {
            while (true) {
                CmdMessage msg = connector.recv();
                switch (msg.cmd) {
                    case "action_request":
                        CmdMessage response = new CmdMessage("action_back", "true");
                        connector.send(response.toString());
                        break;
                }
            }
//            connector.close();
        }
    }
}