package platform.resource.device;

import platform.pubsub.Channel;
import platform.pubsub.Publisher;
import platform.resource.ResMgrThread;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class DeviceRTThread implements Runnable{
    private static DeviceRTThread instance;
    private static Thread t;
    private DatagramSocket s;
    private InetAddress address;
    private int port;
    private Publisher p;

    // 构造方法私有化
    private DeviceRTThread() {
        try {
            s = new DatagramSocket(8080);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        address = null;
        port = 0;
        p = new Publisher();
    }

    // 静态方法返回该实例
    public static DeviceRTThread getInstance(){
        // 第一次检查instance是否被实例化出来，如果没有进入if块
        if(instance == null) {
            synchronized (DeviceRTThread.class) {
                // 某个线程取得了类锁，实例化对象前第二次检查instance是否已经被实例化出来，如果没有，才最终实例出对象
                if (instance == null) {
                    instance = new DeviceRTThread();
                }
            }
        }
        return instance;
    }

    @Override
    public void run() {
        //receive msg from car than publish
        while (true) {
            try {
                byte[] data = new byte[1024];
                DatagramPacket packet = new DatagramPacket(data, data.length);
                s.receive(packet);
                p.publish("sensor", new String(data, 0 , packet.getLength()));
                if (address == null) {
                    address = packet.getAddress();
                    port = packet.getPort();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void Close() {
        if (s != null) {
            s.close();
        }
    }

    // receive msg from channel than transmit to car
    public void transmit(String msg) {
        if (address != null) {
            try {
                byte[] data = msg.getBytes();
                DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
                s.send(packet);
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
