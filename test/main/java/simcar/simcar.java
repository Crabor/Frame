package simcar;

import common.socket.UDP;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import platform.communication.socket.Cmd;
import platform.communication.socket.CmdRet;

public class simcar {
    public static void main(String[] args) {
        new Thread(() -> {
            while (true) {
                Cmd cmd = new Cmd(UDP.recv(8081));
                // if (cmd.args[0] is not ctx name)
                // continue;
                String ret = "";
                switch (cmd.cmd) {
                    case "sensor_on":
                    case "sensor_off":
                    case "actuator_on":
                    case "actuator_off":
                    case "sensor_alive":
                    case "actuator_alive":
                        //TODO
                        ret = "true";
                        break;
                    case "sensor_get":
                        System.out.println("recv:" + cmd);
                        ret = "20";
                        break;
                    case "actuator_set":
                        System.out.println("recv:" + cmd);
                        ret = "true";
                        break;
                }
                CmdRet cmdRet = new CmdRet(cmd, ret);
                UDP.send("127.0.0.1", 8080, cmdRet.toJSONString());
//                System.out.println("send:" + cmdRet);
            }
        }).start();
    }
}
