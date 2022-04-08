package platform.resource.driver;

import platform.pubsub.AbstractSubscriber;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UIDriver extends AbstractSubscriber implements Runnable {
    private Thread t;
    private DatagramSocket socket;
    private int serverPort;
    private InetAddress clientAddress;
    private int clientPort;

    public UIDriver(int serverPort, String clientAddress, int clientPort) {
        // TODO 貌似不会调用Abstract的构造函数
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
        //receive msg from UI than do sth
        while (true) {
            try {
                byte[] data = new byte[1024];
                DatagramPacket packet = new DatagramPacket(data, data.length);
                socket.receive(packet);
                String sensorData = new String(data, 0 , packet.getLength());
                System.out.println("ud recv: " + sensorData);
//                publish("sensor", sensorData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void start() {
        if (t == null) {
            t = new Thread (this, getClass().getSimpleName());
            t.start ();
        }
    }

    @Override
    public void onMessage(String channel, String msg) {
        //receive msg from xxx channel than transmit to UI
//        System.out.println("ud send: " + s2);
        try {
            byte[] data = msg.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, clientAddress, clientPort);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSubscribed(String channel, long subChannelCount) {

    }

    @Override
    public void onUnsubscribed(String channel, long subChannelCount) {

    }
}
