package platform.comm.socket;

import com.alibaba.fastjson.JSONObject;
import platform.comm.socket.UDP;
import platform.struct.CmdRet;
import platform.util.Util;

import java.util.Collection;

public class Cmd {
    public static void send(String cmd, Collection<String> args) {
        UDP.send(Util.formatCommand(cmd, Util.collectionToString(args, " ")));
    }

    public static void send(String cmd, String args) {
        UDP.send(Util.formatCommand(cmd, args));
    }

    //此方法只允许在DeviceDriver类的run方法中调用，望周知
    public static CmdRet recv() {
        return Util.decodeCommandRet(UDP.recv());
    }
}
