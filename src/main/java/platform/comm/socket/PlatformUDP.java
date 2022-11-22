package platform.comm.socket;

import platform.config.Configuration;
import platform.struct.Cmd;
import platform.struct.CmdRet;
import platform.util.Util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class PlatformUDP {
    public static void send(Cmd cmd) {
        UDP.send("255.255.255.255", Configuration.getUdpConfig().getClientPort(), cmd.toJSONString());
    }

    //此方法只允许在DeviceDriver类的run方法中调用，望周知
    public static CmdRet recv() {
        return new CmdRet(UDP.recv(Configuration.getUdpConfig().getServerPort()));
    }
}
