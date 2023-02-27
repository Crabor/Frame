package sensorDriver;

import com.alibaba.fastjson.JSONObject;
import common.socket.UDP;
import common.struct.SensorData;
import platform.communication.socket.Cmd;
import platform.communication.socket.CmdRet;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class zlyDriver {
    public static void main(String[] args) {
        List<String> data = new ArrayList<>();
        try(InputStream inputStream = new FileInputStream("Resources/zlyTest/testData.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader)){
            String line;
            while((line = bufferedReader.readLine()) != null){
                data.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        AtomicInteger cnt = new AtomicInteger();

        new Thread(() -> {
            while (true) {
                Cmd cmd = new Cmd(UDP.recv(8081));
                // if (cmd.args[0] is not ctx name)
                // continue;
                String ret = "";
                switch (cmd.cmd) {
                    case "sensor_on":
                    case "actor_on":
                    case "actor_alive":
                        ret = "true";
                        break;
                    case "sensor_off":
                    case "actor_off":
                        ret = "false";
                        break;
                    case "sensor_alive":
                        if(cmd.args[0].equalsIgnoreCase("car")){
                            ret = "true";
                        }
                        else{
                            ret = "false";
                        }
                        break;
                    case "sensor_get":
                        System.out.println("recv:" + cmd);
                        String[] fields = {"timeStamp", "carId", "longitude", "latitude", "speed", "direction", "state"};
                        String[] values = data.get(cnt.getAndIncrement()).split(",");
                        SensorData sensorData = new SensorData(fields, values);
                        ret = sensorData.toString();
                        break;
                    case "actor_set":
                        //System.out.println("recv:" + cmd);
                        ret = "true";
                        break;
                }
                CmdRet cmdRet = new CmdRet(cmd, ret);
                UDP.send("127.0.0.1", 8080, cmdRet.toJSONString());
//                System.out.println("send:" + cmdRet);
            }
        }).start();
    }
}
