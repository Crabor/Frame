package platform.resource.driver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import platform.pubsub.AbstractSubscriber;
import platform.service.inv.CancerServer;
import platform.util.Util;

import java.io.IOException;
import java.net.*;

import static java.lang.Thread.sleep;

public class DeviceDriver extends AbstractSubscriber implements Runnable {
    private Thread t;
    private DatagramSocket socket;
    private int serverPort;
    private InetAddress clientAddress;
    private int clientPort;

//    private static final Log logger = LogFactory.getLog(DeviceDriver.class);

    public DeviceDriver(int serverPort, String clientAddress, int clientPort) {
        this.serverPort = serverPort;
        this.clientPort = clientPort;
        try {
            this.clientAddress = InetAddress.getByName(clientAddress);
            socket = new DatagramSocket(this.serverPort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        //receive msg from car than publish to sensor channel
        while (true) {
            // wang hui yan
            try {
                byte[] data = new byte[1024];
                DatagramPacket packet = new DatagramPacket(data, data.length);
                socket.receive(packet);
                String sensorData = new String(data, 0, packet.getLength());
//                Thread.sleep(50);
//                String sensorData = Util.randomJSONCarData();
                logger.debug("dd recv: " + sensorData);
                publish("sensor", sensorData);
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
        //System.out.println("dd send: " + msg);
        //receive msg from actor channel than transmit to car
        try {
            JSONObject jo = new JSONObject();
            jo.put("channel", channel);
            jo.put("message", msg);
            byte[] data = jo.toJSONString().getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, clientAddress, clientPort);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
