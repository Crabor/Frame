package platform.testUDP;

import platform.comm.socket.UDP;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class testUDP {
    public static void main(String[] args) {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    UDP.send(InetAddress.getLocalHost(), 333, "hello");
                } catch (InterruptedException | UnknownHostException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        new Thread(() -> {
            while (true) {
                System.out.println(UDP.recv(333));
            }
        }).start();
    }
}
