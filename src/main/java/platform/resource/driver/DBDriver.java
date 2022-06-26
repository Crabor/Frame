package platform.resource.driver;

import io.lettuce.core.KeyValue;
import io.lettuce.core.api.sync.RedisCommands;
import platform.pubsub.AbstractSubscriber;

import java.net.DatagramSocket;
import java.net.InetAddress;

public class DBDriver extends AbstractSubscriber implements Runnable {
    private Thread t;

    @Override
    public void run() {
        //receive msg from UI than do sth
        while (true) {
            RedisCommands<String, String> sync = commonConn.sync();
            switch (sync.brpop(0, "cmd").getValue()) {
                //TODO: cmd
                case "a" :
                    break;
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
        //System.out.println("ui: "+channel+":"+msg);
//        JSONObject jo = new JSONObject();
//        jo.put("channel", channel);
//        jo.put("message", msg);
//        try {
//            byte[] data = jo.toJSONString().getBytes();
//            DatagramPacket packet = new DatagramPacket(data, data.length, clientAddress, clientPort);
//            socket.send(packet);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        RedisCommands<String, String> sync = commonConn.sync();
        sync.rpush(channel, msg);
    }

    @Override
    public void onSubscribed(String channel, long subChannelCount) {

    }

    @Override
    public void onUnsubscribed(String channel, long subChannelCount) {

    }
}
