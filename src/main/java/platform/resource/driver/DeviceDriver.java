package platform.resource.driver;

import com.alibaba.fastjson.JSONObject;
import platform.comm.socket.PlatformUDP;
import platform.struct.Cmd;
import platform.config.Configuration;
import platform.comm.pubsub.AbstractSubscriber;
import platform.struct.CmdRet;
import platform.util.Util;

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
                    case "actuator_on":
                    case "actuator_off":
                    case "sensor_on":
                    case "sensor_off":
                        if (Boolean.parseBoolean(cmdRet.ret)) {
                            logger.info(cmdRet.getCmdMsg() + " succeed!");
                        } else {
                            logger.warn(cmdRet.getCmdMsg() + " failed!");
                        }
                        break;
                    case "actuator_set":
                    case "channel_message":
                        if (!Boolean.parseBoolean(cmdRet.ret)) {
                            logger.warn(cmdRet.getCmdMsg() + " failed!");
                        }
                        break;
                    case "actuator_alive":
                        Configuration.getResourceConfig().getActuatorsConfig().
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
//                        logger.debug("dd recv: " + cmdRet);
                        if (!cmdRet.ret.equals("@#$%")) {
                            String msg = Util.formatToJsonString(cmdRet.args[0], cmdRet.ret);
                            publish("sensor", 0, msg);
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
        //receive msg from actuator channel than transmit to car
        JSONObject jo = new JSONObject(2);
        jo.put("channel", channel);
        jo.put("message", msg);
        Cmd channel_message = new Cmd("channel_message", jo.toJSONString());
        PlatformUDP.send(channel_message);
//        logger.info(channel_message);
    }
}
