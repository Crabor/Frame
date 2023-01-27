package platform.app;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import platform.comm.pubsub.AbstractSubscriber;
import platform.comm.socket.UDP;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class AppDriver extends AbstractSubscriber implements Runnable {
    private Socket socket;
    private String clientIP;
    private int clientUDPPort;

    public AppDriver(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void onMessage(String channel, String msg) {
        JSONObject jo = new JSONObject(2);
        jo.put("channel", channel);
        jo.put("msg", msg);
        //TODO:
        UDP.send(clientIP, clientUDPPort, jo.toJSONString());
    }

    @Override
    public void run() {
        try {
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
            String msgFromClient;
            while ((msgFromClient = inFromClient.readLine()) != null) {
                // TODO:
                JSONObject retJson = new JSONObject();
                JSONObject jo = JSON.parseObject(msgFromClient);
                String api = jo.getString("api");
                if (api.equalsIgnoreCase("register_app")) {
                    clientIP = socket.getInetAddress().getHostAddress();
                    clientUDPPort = AppMgrThread.getNewSensorDataChannelUDPPort(socket);
                    retJson.put("state", true);
                    retJson.put("udp_port", clientUDPPort);
                    //TODO: 还有一些初始化工作
                }

                outToClient.writeBytes(retJson.toJSONString() + '\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
