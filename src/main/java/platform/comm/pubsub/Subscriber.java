package platform.comm.pubsub;

public interface Subscriber {
    void onMessage(String channel, String msg);
}
