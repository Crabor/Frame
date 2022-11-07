package platform.resource.driver;

import com.alibaba.fastjson.JSONObject;
import platform.comm.socket.Cmd;
import platform.comm.socket.UDP;
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
//                String sensorData = new String(data, 0, packet.getLength());
////                Thread.sleep(50);
////                String sensorData = Util.randomJSONCarData();
//                logger.debug("dd recv: " + sensorData);
//                publish("sensor", 0, sensorData);
                CmdRet cmdRet = Cmd.recv();
                switch (cmdRet.cmd) {
                    case "actuator_on":
                    case "actuator_off":
                    case "sensor_on":
                    case "sensor_off":
                        for (int i = 0; i < cmdRet.rets.length; i++) {
                            if (Boolean.parseBoolean(cmdRet.rets[i])) {
                                logger.info(cmdRet.cmd + " " + cmdRet.args[i] + " succeed!");
                            } else {
                                logger.warn(cmdRet.cmd + " " + cmdRet.args[i] + " failed!");
                            }
                        }
                        break;
                    case "actuator_set":
                    case "channel_message":
                    case "sync":
                        for (int i = 0; i < cmdRet.rets.length; i++) {
                            if (!Boolean.parseBoolean(cmdRet.rets[i])) {
                                logger.warn(cmdRet.cmd + " " + cmdRet.args[i] + " failed!");
                            }
                        }
                        break;
                    case "actuator_alive":
                        for (int i = 0; i < cmdRet.rets.length; i++) {
                            Configuration.getResourceConfig().getActuatorsConfig().
                                    get(cmdRet.args[i]).
                                    setAlive(Boolean.parseBoolean(cmdRet.rets[i]));
                        }
                        break;
                    case "sensor_alive":
                        for (int i = 0; i < cmdRet.rets.length; i++) {
                            Configuration.getResourceConfig().getSensorsConfig().
                                    get(cmdRet.args[i]).
                                    setAlive(Boolean.parseBoolean(cmdRet.rets[i]));
                        }
                        break;
                    case "sensor_get":
                        publish("sensor", 0, Util.keysValuesToJsonString(cmdRet.args, cmdRet.rets));
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
        logger.debug("dd send: " + msg);
        //receive msg from actuator channel than transmit to car
        JSONObject jo = new JSONObject();
        jo.put("channel", channel);
        jo.put("message", msg);
        Cmd.send("channel_message", jo.toJSONString());
    }
}
