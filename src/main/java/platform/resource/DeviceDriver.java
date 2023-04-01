package platform.resource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import common.socket.AbstractTCP;
import common.socket.CmdMessage;
import common.socket.CmdMessageGrpIds;
import common.socket.TCP;
import common.struct.enumeration.DeviceType;
import platform.communication.pubsub.Publisher;
import platform.communication.socket.PlatformUDP;
import platform.communication.socket.Cmd;
import platform.config.ActorConfig;
import platform.config.Configuration;
import platform.communication.pubsub.AbstractSubscriber;
import platform.communication.socket.CmdRet;
import common.util.Util;
import platform.config.SensorConfig;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

import static java.lang.Thread.sleep;

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
                deviceType = DeviceType.fromString(joo.getString("deviceType"));
                if (deviceType == DeviceType.SENSOR) {
                    sensorConfig = new SensorConfig(joo);
                    Configuration.getResourceConfig().getSensorsConfig().put(deviceName, sensorConfig);
                } else if (deviceType == DeviceType.ACTOR) {
                    actorConfig = new ActorConfig(joo);
                    Configuration.getResourceConfig().getActorsConfig().put(deviceName, actorConfig);
                } else if (deviceType == DeviceType.HYBRID) {
                    sensorConfig = new SensorConfig(joo);
                    actorConfig = new ActorConfig(joo);
                    Configuration.getResourceConfig().getSensorsConfig().put(deviceName, sensorConfig);
                    Configuration.getResourceConfig().getActorsConfig().put(deviceName, actorConfig);
                }
                subscribe(deviceName + "_request");
            }

            CmdMessage ret = new CmdMessage("register_back", "true");
            tcp.send(ret.toString());
            logger.info(String.format("[Platform -> %s]: %s", deviceName, ret));
        }
//
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        tcp.recv();

//        while(true) {
//            try {
//                Thread.sleep(1000);
//                logger.info(tcp.getSocket().getKeepAlive());
//            } catch (SocketException e) {
//                throw new RuntimeException(e);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            logger.info(String.format("[%s]: %s", deviceName, tcp.getSocket().isClosed()));
//        }
//        while (true) {
//            try {
//                sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            logger.info(String.format("[%s]: %s", deviceName, tcp.getSocket().isClosed()));
//        }
        //receive msg from car than publish to sensor channel
//        try {
//            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream("Resources/taxiTest/testdata.txt"), StandardCharsets.UTF_8);
//            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//            String line = null;
//            while((line = bufferedReader.readLine()) != null){
//                Thread.sleep(5000);
//                JSONObject jsonObject = new JSONObject();
//
//                String[] values = line.split(";");
//                jsonObject.put("taxis", values[0]);
//                jsonObject.put("front", values[1]);
//                jsonObject.put("back", values[2]);
//                jsonObject.put("left", values[3]);
//                jsonObject.put("right", values[4]);
//
//                logger.debug("dd recv: " + jsonObject.toJSONString());
//                publish("sensor", 0, jsonObject.toJSONString());
//            }
//            bufferedReader.close();
//            inputStreamReader.close();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }

//        while (true) {
//            // wang hui yan
//            try {
//                CmdRet cmdRet = PlatformUDP.recv();
//                logger.debug("dd recv: " + cmdRet);
//                switch (cmdRet.cmd) {
//                    case "actor_on":
//                    case "actor_off":
//                    case "sensor_on":
//                    case "sensor_off":
//                        if (Boolean.parseBoolean(cmdRet.ret)) {
//                            logger.info(cmdRet.getCmdMsg() + " succeed!");
//                        } else {
//                            logger.warn(cmdRet.getCmdMsg() + " failed!");
//                        }
//                        break;
//                    case "actor_set":
////                        logger.info(cmdRet);
////                        Publisher.publish(cmdRet.args[0], Integer.parseInt(cmdRet.args[1]), cmdRet.ret);
//                        break;
//                    case "channel_message":
//                        if (!Boolean.parseBoolean(cmdRet.ret)) {
//                            logger.warn(cmdRet.getCmdMsg() + " failed!");
//                        }
//                        break;
//                    case "actor_alive":
//                        Configuration.getResourceConfig().getActorsConfig().
//                                get(cmdRet.args[0]).
//                                setAlive(Boolean.parseBoolean(cmdRet.ret));
//                        break;
//                    case "sensor_alive":
////                        logger.info(cmdRet);
////                        logger.info(cmdRet.args[0]);
////                        logger.info(Boolean.parseBoolean(cmdRet.ret));
//                        Configuration.getResourceConfig().getSensorsConfig().
//                                get(cmdRet.args[0]).
//                                setAlive(Boolean.parseBoolean(cmdRet.ret));
//                        break;
//                    case "sensor_get":
////                        logger.info("dd recv: " + cmdRet);
//                            //TODO:时都需要再组装成json的信息？
////                            String msg = Util.formatToJsonString(cmdRet.args[0], cmdRet.ret);
//                            // cmdRet.args[0] = "front"
//                            // cmdRet.ret = "20"
//                            // msg = {"front": "20"}
//                        for (int i = 1; i < cmdRet.args.length; i++) {
//                            Publisher.publish(cmdRet.args[0], Integer.parseInt(cmdRet.args[i]), cmdRet.ret);
//                        }
//                        break;
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
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
