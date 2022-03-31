package platform.resource.device;

import platform.pubsub.AbstractSubscriber;

import java.io.IOException;
import java.net.*;

public class DeviceDriver extends AbstractSubscriber implements Runnable {
    private Thread t;
    private DatagramSocket socket;
    private int serverPort;
    private InetAddress clientAddress;
    private int clientPort;

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
    public void onMessage(String s, String s2) {
        System.out.println("send: " + s2);
        //receive msg from actor channel than transmit to car
        try {
            byte[] data = s2.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, clientAddress, clientPort);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSubscribed(String s, long l) {

    }

    @Override
    public void onUnsubscribed(String s, long l) {

    }

    @Override
    public void run() {
        //receive msg from car than publish to sensor channel
        while (true) {
            try {
                byte[] data = new byte[1024];
                DatagramPacket packet = new DatagramPacket(data, data.length);
                socket.receive(packet);
                String sensorData = new String(data, 0 , packet.getLength());
//                System.out.println("dd recv: " + sensorData);
                publish("sensor", sensorData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void start() {
        if (t == null) {
            t = new Thread (this, "ResMgrThread");
            t.start ();
        }
    }

}
