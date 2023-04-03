package wrapper;

import common.socket.CmdMessage;

import java.io.IOException;

public class WrapperDemo2 {
    public static void main(String[] args) throws IOException {
        String config = "{\"name\":\"YellowCar\"," +
                "\"deviceType\":\"Sensor\"," +
                "\"fields\":[\"speed\",\"longitude\",\"timestamp\"]}";
        WrapperRemoteConnector connector = WrapperRemoteConnector.getInstance();
        if (connector.register("127.0.0.1", 9091, config)) {
            int i = 0;
            boolean flag = true;
            while (true) {
                flag = (i++ % 20 == 0) != flag;
                CmdMessage msg = connector.recv();
                switch (msg.cmd) {
                    case "sensory_request":
                        CmdMessage response = flag ?
                                new CmdMessage("sensory_back",
                                        "{\"speed\":10.0,\"longitude\":20.0," +
                                                "\"timestamp\":30.0}") :
                                new CmdMessage("sensory_back",
                                        "{\"speed\":20.0,\"longitude\":30.0," +
                                                "\"timestamp\":10.0}");
                        connector.send(response.toString());
                        System.out.println("[Wrapper]: " + response);
                        break;
                }
            }
//            connector.close();
        }
    }
}