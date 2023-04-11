package platform.resource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import common.socket.AbstractTCP;
import common.socket.CmdMessage;
import common.socket.CmdMessageGrpIds;
import common.socket.TCP;
import common.struct.enumeration.DeviceType;
import platform.communication.pubsub.Publisher;
import platform.config.ActorConfig;
import platform.config.Configuration;
import platform.communication.pubsub.AbstractSubscriber;
import platform.config.SensorConfig;

import java.io.IOException;
import java.net.Socket;

public class DeviceDriver extends AbstractSubscriber implements Runnable {
    private Thread t;
    private TCP tcp;
    private SensorConfig sensorConfig = null;
    private ActorConfig actorConfig = null;
    private DeviceType deviceType;
    private String deviceName;

//    private static final Log logger = LogFactory.getLog(DeviceDriver.class);

    public DeviceDriver(Socket socket) {
        this.tcp = new DeviceDriverTCP(socket, false);
    }

    public class DeviceDriverTCP extends AbstractTCP {
        public DeviceDriverTCP(Socket socket, boolean lockFlag) {
            super(socket, lockFlag);
        }

        public DeviceDriverTCP(Socket socket) {
            super(socket);
        }

        @Override
        public void close() {
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
//            disConnectPlatform();
        }

        @Override
        public void callback() {
            //TODO
            logger.error(String.format("[%s]: TCP connection is broken. Set the status to off", deviceName));
            if (deviceType == DeviceType.SENSOR) {
                sensorConfig.setAlive(false);
            } else if (deviceType == DeviceType.ACTOR) {
                actorConfig.setAlive(false);
            } else if (deviceType == DeviceType.HYBRID) {
                sensorConfig.setAlive(false);
                actorConfig.setAlive(false);
            }
            unsubscribe(deviceName + "_request");
//            String name = "AppDriver";
//            if (appConfig != null) {
//                name = appConfig.getAppName();
//            }
//            logger.error(String.format("[%s]: TCP connection is broken. Start releasing app resources...", name));
//            if (disConnectPlatform().equals("{\"state\":true}")) {
//                logger.info(String.format("[%s]: App resource released successfully.", name));
//            } else {
//                logger.error(String.format("[%s]: App resource released failed.", name));
//            }
        }
    }


    @Override
    public void run() {
        String msgFromClient;
        if ((msgFromClient = tcp.recv()) != null) {
            CmdMessage cmdMessage = new CmdMessage(msgFromClient);
            logger.info(String.format("[Wrapper -> Platform]: %s", cmdMessage));
            JSONObject joo = JSON.parseObject(cmdMessage.message);

            if (cmdMessage.cmd.equalsIgnoreCase("register")) {
                deviceName = joo.getString("name");
                deviceType = DeviceType.fromString(joo.getString("type"));
                if (deviceType == DeviceType.SENSOR) {
                    if (Configuration.getResourceConfig().getSensorsConfig().containsKey(deviceName)) {
                        sensorConfig = Configuration.getResourceConfig().getSensorsConfig().get(deviceName);
                        sensorConfig.setAlive(true);
                    } else {
                        sensorConfig = new SensorConfig(joo);
                        Configuration.getResourceConfig().getSensorsConfig().put(deviceName, sensorConfig);
                    }
                } else if (deviceType == DeviceType.ACTOR) {
                    if (Configuration.getResourceConfig().getActorsConfig().containsKey(deviceName)) {
                        actorConfig = Configuration.getResourceConfig().getActorsConfig().get(deviceName);
                        actorConfig.setAlive(true);
                    } else {
                        actorConfig = new ActorConfig(joo);
                        Configuration.getResourceConfig().getActorsConfig().put(deviceName, actorConfig);
                    }
                } else if (deviceType == DeviceType.HYBRID) {
                    if (Configuration.getResourceConfig().getSensorsConfig().containsKey(deviceName)) {
                        sensorConfig = Configuration.getResourceConfig().getSensorsConfig().get(deviceName);
                        sensorConfig.setAlive(true);
                    } else {
                        sensorConfig = new SensorConfig(joo);
                        Configuration.getResourceConfig().getSensorsConfig().put(deviceName, sensorConfig);
                    }
                    if (Configuration.getResourceConfig().getActorsConfig().containsKey(deviceName)) {
                        actorConfig = Configuration.getResourceConfig().getActorsConfig().get(deviceName);
                        actorConfig.setAlive(true);
                    } else {
                        actorConfig = new ActorConfig(joo);
                        Configuration.getResourceConfig().getActorsConfig().put(deviceName, actorConfig);
                    }
                }
                subscribe(deviceName + "_request");
            }

            CmdMessage ret = new CmdMessage("register_back", "true");
            tcp.send(ret.toString());
            logger.info(String.format("[Platform -> %s]: %s", deviceName, ret));
            tcp.setLockFlag(true);

            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                CmdMessage alive = new CmdMessage("alive_request", null);
                if (!tcp.send(alive.toString())) {
                    break;
                }
                tcp.unlock();
            }
        }
    }

    public void start() {
        if (t == null) {
            t = new Thread(this, getClass().getName());
            t.start();
        }
    }

    @Override
    public void onMessage(String channel, String msg) {
        if (deviceType == DeviceType.SENSOR && sensorConfig.isAlive() ||
        deviceType == DeviceType.ACTOR && actorConfig.isAlive() ||
        deviceType == DeviceType.HYBRID && sensorConfig.isAlive() && actorConfig.isAlive()) {
            CmdMessageGrpIds cmdMessageGrpIds = new CmdMessageGrpIds(msg);
            CmdMessage send = cmdMessageGrpIds.getCmdMessage();
            tcp.send(send.toString());
            logger.info(String.format("[Platform -> %s]: %s", deviceName, send));
            CmdMessage recv = new CmdMessage(tcp.recv());
            logger.info(String.format("[%s -> Platform]: %s", deviceName, recv));

            for (Integer grpId : cmdMessageGrpIds.grpIds) {
                Publisher.publish(deviceName, grpId, recv.message);
            }
        }
    }
}
