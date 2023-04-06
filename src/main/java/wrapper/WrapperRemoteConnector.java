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
    private String wrapperName;
    
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
            CmdMessage recvCmd = new CmdMessage(recv);
            if (!recvCmd.cmd.equalsIgnoreCase("alive_request")) {
                logger.info(String.format("[%s]: recv() -> %s", wrapperName, recv));
            }
            return new CmdMessage(recv);
        }
        logger.info(String.format("[%s]: recv() -> null", wrapperName));
        return null;
    }

    public void send(String str) {
        tcp.send(str);
        logger.info(String.format("[%s]: send(%s)", wrapperName, str));
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
            logger.info(String.format("[%s]: TCP connection is broken.", wrapperName));
        }
    }

    public boolean register(String ip, int port, String deviceConfig) {
        wrapperName = JSON.parseObject(deviceConfig).getString("name");
        //log set
        Util.createNewLog4jProperties(wrapperName);
        PropertyConfigurator.configure("Resources/config/log/log4j-" + wrapperName + ".properties");
        File file = new File("output/log/" + wrapperName + "/");
        Util.deleteDir(file);

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
        logger.info(String.format("[%s]: register(%s, %d, %s) -> %b", wrapperName, ip, port, deviceConfig, state));
        return state;
    }

    public boolean shutdown() {
        tcp.close();
        logger.info(String.format("[%s]: shutdown()", wrapperName));
        return true;
    }
}
