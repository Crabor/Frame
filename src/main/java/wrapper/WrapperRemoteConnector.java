package wrapper;

import app.AppRemoteConnector;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import common.socket.AbstractTCP;
import common.socket.CmdMessage;
import common.socket.TCP;
import common.util.Util;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class WrapperRemoteConnector {
    private static WrapperRemoteConnector instance;
    private TCP tcp = null;
    private Log logger = LogFactory.getLog(WrapperRemoteConnector.class);
    private String wrapperName = null;

    static {
        //log set
        String pid = "wrapper" + ProcessHandle.current().pid();
        Util.createNewLog4jProperties(pid);
        PropertyConfigurator.configure("Resources/config/log/log4j-" + pid + ".properties");
        File file = new File("output/log/" + pid + "/");
        Util.deleteDir(file);
    }
    
    private WrapperRemoteConnector() {

    }

    public Log getLogger() {
        return logger;
    }
    
    public static WrapperRemoteConnector getInstance() {
        if (instance == null) {
            synchronized (WrapperRemoteConnector.class) {
                if (instance == null) {
                    instance = new WrapperRemoteConnector();
                }
            }
        }
        return instance;
    }

    public CmdMessage recv() {
        String recv = tcp.recv();
        if (recv != null) {
            return new CmdMessage(recv);
        }
        return null;
    }

    public void send(String str) {
        tcp.send(str);
    }

    public void close() {
        shutdown();
    }

    public class WrapperRemoteConnectorTCP extends AbstractTCP {
        public WrapperRemoteConnectorTCP(Socket socket, boolean lockFlag) {
            super(socket, lockFlag);
        }

        public WrapperRemoteConnectorTCP(Socket socket) {
            super(socket);
        }


        @Override
        public void close() {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void callback() {
            logger.info("[WrapperConnector]: TCP connection is broken.");
        }
    }

    public boolean register(String ip, int port, String deviceConfig) {
        CmdMessage send = new CmdMessage("register", deviceConfig);
        boolean state = false;
        try {
            tcp = new WrapperRemoteConnectorTCP(new Socket(ip, port), false);
            tcp.send(send.toJSONString());

            String recv = tcp.recv();
            if (recv != null) {
                CmdMessage recvCmd = new CmdMessage(recv);
                if (recvCmd.cmd.equals("register_back")) {
                    state = Boolean.parseBoolean(recvCmd.message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            tcp.close();
        }
        logger.info(String.format("[Wrapper]: register(%s, %d, %s) -> %s", ip, port, deviceConfig, state));
        wrapperName = JSON.parseObject(deviceConfig).getString("name");
        return state;
    }

    public boolean shutdown() {
        tcp.close();
        logger.info("[Wrapper]: shutdown() -> true");
        return true;
    }
}
