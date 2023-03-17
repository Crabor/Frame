package platform.resource.driver;

import com.alibaba.fastjson.JSONObject;
import platform.communication.pubsub.Publisher;
import platform.communication.socket.PlatformUDP;
import platform.communication.socket.Cmd;
import platform.config.Configuration;
import platform.communication.pubsub.AbstractSubscriber;
import platform.communication.socket.CmdRet;
import common.util.Util;

import static java.lang.Thread.sleep;

public class DeviceDriver extends AbstractSubscriber implements Runnable {
    private Thread t;

//    private static final Log logger = LogFactory.getLog(DeviceDriver.class);

    public DeviceDriver() {

    }


    @Override
    public void run() {
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

        while (true) {
            // wang hui yan
            try {
                CmdRet cmdRet = PlatformUDP.recv();
                logger.debug("dd recv: " + cmdRet);
                switch (cmdRet.cmd) {
                    case "actor_on":
                    case "actor_off":
                    case "sensor_on":
                    case "sensor_off":
                        if (Boolean.parseBoolean(cmdRet.ret)) {
                            logger.info(cmdRet.getCmdMsg() + " succeed!");
                        } else {
                            logger.warn(cmdRet.getCmdMsg() + " failed!");
                        }
                        break;
                    case "actor_set":
//                        logger.info(cmdRet);
//                        Publisher.publish(cmdRet.args[0], Integer.parseInt(cmdRet.args[1]), cmdRet.ret);
                        break;
                    case "channel_message":
                        if (!Boolean.parseBoolean(cmdRet.ret)) {
                            logger.warn(cmdRet.getCmdMsg() + " failed!");
                        }
                        break;
                    case "actor_alive":
                        Configuration.getResourceConfig().getActorsConfig().
                                get(cmdRet.args[0]).
                                setAlive(Boolean.parseBoolean(cmdRet.ret));
                        break;
                    case "sensor_alive":
//                        logger.info(cmdRet);
//                        logger.info(cmdRet.args[0]);
//                        logger.info(Boolean.parseBoolean(cmdRet.ret));
                        Configuration.getResourceConfig().getSensorsConfig().
                                get(cmdRet.args[0]).
                                setAlive(Boolean.parseBoolean(cmdRet.ret));
                        break;
                    case "sensor_get":
//                        logger.info("dd recv: " + cmdRet);
                            //TODO:时都需要再组装成json的信息？
//                            String msg = Util.formatToJsonString(cmdRet.args[0], cmdRet.ret);
                            // cmdRet.args[0] = "front"
                            // cmdRet.ret = "20"
                            // msg = {"front": "20"}
                        for (int i = 1; i < cmdRet.args.length; i++) {
                            Publisher.publish(cmdRet.args[0], Integer.parseInt(cmdRet.args[i]), cmdRet.ret);
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
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
        logger.debug("dd send channel_message: " + msg);
        //receive msg from actor channel than transmit to car
        JSONObject jo = new JSONObject(2);
        jo.put("channel", channel);
        jo.put("message", msg);
        Cmd channel_message = new Cmd("channel_message", jo.toJSONString());
        PlatformUDP.send(channel_message);
//        logger.info(channel_message);
    }
}
