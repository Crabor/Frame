package platform.testUDP;

import platform.Platform;
import platform.app.AbstractApp;
import platform.comm.socket.UDP;
import platform.comm.socket.Cmd;
import platform.comm.socket.CmdRet;
import platform.struct.CmdType;
import platform.struct.ServiceType;

public class zshApp extends AbstractApp {
    @Override
    protected void customizeCtxServer() {
        config.registerSensor("left");
    }

    @Override
    public void iter(String channel, String msg) {
        logger.debug("app recv: " + msg);
        //如果为sensor1

        //sensor to ctx1

        //transmit ctx1 to platform
//        Cmd cmd = new Cmd("sensor_get", "ctx1");
//        CmdRet cmdRet = new CmdRet(cmd, "ctx1 value");
//        UDP.send("127.0.0.1", 8080, cmdRet.toJSONString());

    }

    @Override
    public void run() {
        //driver
        new Thread(() -> {
            while (true) {
                Cmd cmd = new Cmd(UDP.recv(8081));
                // if (cmd.args[0] is not ctx name)
                // continue;
                String ret = "";
                switch (cmd.cmd) {
                    case "sensor_on":
                    case "sensor_off":
                        //TODO
                        ret = "true";
                        break;
                    case "sensor_alive":
                        //TODO
                        ret = "true";
                        break;
                    case "sensor_get":
                        ret = "20";
                        break;
                }
                CmdRet cmdRet = new CmdRet(cmd, ret);
                UDP.send("127.0.0.1", 8080, cmdRet.toJSONString());
            }
        }).start();

        //your app
    }
}
