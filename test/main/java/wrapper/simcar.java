package wrapper;

import common.socket.UDP;
import platform.communication.socket.Cmd;
import platform.communication.socket.CmdRet;

public class simcar {
    public static void main(String[] args) {
        while (true) {
            Cmd cmd = new Cmd(UDP.recv(8081));
            String ret = "";
//                System.out.println(cmd.cmd);
            switch (cmd.cmd) {
                case "sensor_on":
                case "sensor_off":
                case "actor_on":
                case "actor_off":
                case "sensor_alive":
                case "actor_alive":
                    //TODO
                    ret = "true";
                    break;
                case "sensor_get":
                    System.out.println("recv:" + cmd);
                    ret = "{\"default\":20}";
                    break;
                case "actor_set":
                    System.out.println("recv:" + cmd);
                    ret = "true";
                    break;
            }
            CmdRet cmdRet = new CmdRet(cmd, ret);
            UDP.send("127.0.0.1", 8080, cmdRet.toJSONString());
//                System.out.println("send:" + cmdRet);
        }
    }
}