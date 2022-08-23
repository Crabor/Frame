package platform.testnative;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import platform.testunitycar.Actor;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class NativeApp {
    public static void main(String[] args) throws Exception {
        //初始化
        double constLeft = 19.5;
        InetAddress clientAddress = InetAddress.getByName("127.0.0.1");
        int clientPort = 8081;
        int serverPort = 8080;
        DatagramSocket socket = new DatagramSocket(serverPort);

        while (true) {
            //获得sensor数据
            byte[] recvData = new byte[1024];
            DatagramPacket recvPacket = new DatagramPacket(recvData, recvData.length);
            socket.receive(recvPacket);
            String sensorData = new String(recvData, 0, recvPacket.getLength());
            JSONObject jo = JSONObject.parseObject(sensorData);

            //过滤sensor数据
            if (Math.abs(jo.getDouble("front")) >= 200
                    || Math.abs(jo.getDouble("left")) >= 200
                    || Math.abs(jo.getDouble("right")) >= 200
                    || Math.abs(jo.getDouble("back")) >= 200) {
                continue;
            }

            //根据sensor数据来做出actor指令
            Actor actor = new Actor(2, constLeft - jo.getDouble("left"), 0);

            //发送actor指令
            JSONObject send = new JSONObject();
            send.put("channel", "actor");
            send.put("message", JSON.toJSONString(actor));
            byte[] sendData = send.toJSONString().getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
            socket.send(sendPacket);
        }
    }
}
