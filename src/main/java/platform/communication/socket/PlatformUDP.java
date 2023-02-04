package platform.communication.socket;

import common.socket.UDP;
import platform.config.Configuration;

public class PlatformUDP {
    public static void send(Cmd cmd) {
        for (int port : Configuration.getUdpConfig().getClientPort()) {
            UDP.send("255.255.255.255", port, cmd.toJSONString());
        }
    }

    //此方法只允许在DeviceDriver类的run方法中调用，望周知
    public static CmdRet recv() {
        return new CmdRet(UDP.recv(Configuration.getUdpConfig().getServerPort()));
    }
}
