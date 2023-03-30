package wrapper;

import common.socket.UDP;
import common.struct.SensorData;
import platform.communication.socket.Cmd;
import platform.communication.socket.CmdRet;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class zshDriver {
    public static void main(String[] args) {
        List<String> data = new ArrayList<>();
        try(InputStream inputStream = new FileInputStream("Resources/simcityTest/contextData_blue.txt");
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
                String ret = "";
                switch (cmd.cmd) {
                    case "sensor_on":
                    case "actor_on":
                    case "actor_alive":
                    case "sensor_alive":
                    case "actor_set":
                        ret = "true";
                        break;
                    case "sensor_get":
                        System.out.println("recv:" + cmd);
                        String[] fields = {"car","state","prev_loc","cur_loc","next_loc","timestamp"};
                        String[] values = data.get(cnt.getAndIncrement()).split(",");
                        SensorData sensorData = new SensorData(fields, values);
                        ret = sensorData.toString();
                        break;
                }
                CmdRet cmdRet = new CmdRet(cmd, ret);
                UDP.send("127.0.0.1", 8080, cmdRet.toJSONString());
            }
        }).start();
    }
}
