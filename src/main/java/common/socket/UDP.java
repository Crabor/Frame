package common.socket;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class UDP {
    private static final Map<Integer, DatagramSocket> sockets = new HashMap<>();
    static {
        try {
            sockets.put(0, new DatagramSocket());
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    public static void close(int port) {
        DatagramSocket socket = sockets.get(port);
        sockets.remove(port);
        socket.close();
    }

    public static void send(String clientAddress, int clientPort, String msg) {
        try {
            byte[] data = msg.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(clientAddress), clientPort);
            sockets.get(0).send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void send(InetAddress clientAddress, int clientPort, String msg) {
        try {
            byte[] data = msg.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, clientAddress, clientPort);
            sockets.get(0).send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String recv(int port) {
        byte[] data = new byte[0];
        DatagramPacket packet = null;
        try {
            data = new byte[1024];
            packet = new DatagramPacket(data, data.length);
            DatagramSocket socket = sockets.computeIfAbsent(port, k -> {
                try {
                    return new DatagramSocket(port);
                } catch (SocketException e) {
                    throw new RuntimeException(e);
                }
            });
            socket.receive(packet);
        } catch (IOException e) {
            //TODO
            return null;
        }
        return new String(data, 0, packet.getLength());
    }
}
