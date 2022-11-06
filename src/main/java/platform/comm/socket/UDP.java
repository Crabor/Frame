package platform.comm.socket;

import com.alibaba.fastjson.JSONObject;
import platform.config.UDPConfig;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDP {
    private static DatagramSocket socket;
    private static int serverPort;
    private static InetAddress clientAddress;
    private static int clientPort;

    public static void Init(UDPConfig config) {
        serverPort = config.getServerPort();
        clientPort = config.getClientPort();
        try {
            clientAddress = InetAddress.getByName(config.getClientAddress());
            socket = new DatagramSocket(serverPort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void send(String msg) {
        try {
            byte[] data = msg.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, clientAddress, clientPort);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String recv() {
        byte[] data = new byte[0];
        DatagramPacket packet = null;
        try {
            data = new byte[1024];
            packet = new DatagramPacket(data, data.length);
            socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(data, 0, packet.getLength());
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public int getServerPort() {
        return serverPort;
    }

    public InetAddress getClientAddress() {
        return clientAddress;
    }

    public int getClientPort() {
        return clientPort;
    }
}
